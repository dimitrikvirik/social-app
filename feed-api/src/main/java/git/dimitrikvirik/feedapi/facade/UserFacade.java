package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.ResponseMapper;
import git.dimitrikvirik.feedapi.mapper.UserMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.feedapi.model.kafka.UserKafka;
import git.dimitrikvirik.feedapi.service.FriendsService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserFacade {


	private final UserService userService;
	private final FriendsService friendsService;

	private final ReactiveKafkaConsumerTemplate<String, UserKafka> kafkaTemplate;


	@PostConstruct
	public void kafkaSubscribe() {
		kafkaTemplate.receiveAutoAck().flatMap(value ->
				{

					UserKafka userKafka = value.value();
					FeedUser feedUser = FeedUser.builder()
							.firstname(userKafka.getFirstName())
							.lastname(userKafka.getLastName())
							.photo(userKafka.getProfile())
							.id(userKafka.getId())
							.build();


					return userService.save(feedUser);
				}
		).log().subscribe();
	}

	public Mono<ResponseEntity<UserDTO>> getUserById(String id, ServerWebExchange exchange) {

		Mono<FeedUser> targetUser = userService.getById(id)
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));

		Mono<UserDTO> response = targetUser.zipWith(userService.currentUser())
			.flatMap(t -> friendsService.findActiveFriendsByUserIds(t.getT1().getId(), t.getT2().getId())
					.defaultIfEmpty(new FeedFriends())
					.map(f -> UserMapper.toUserDTO(t.getT2(), f.getStatus()))
			);

		return ResponseMapper.toResponseEntity(HttpStatus.OK, response);
	}

	public Mono<ResponseEntity<UserResponse>> createUser(FeedUser user) {
		return userService.save(user).map(u -> ResponseEntity.ok(UserMapper.toUserResponse(u)));
	}

}
