package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.model.kafka.UserDTO;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserFacade {


	private final UserService userService;

	private final ReactiveKafkaConsumerTemplate<String, UserDTO> kafkaTemplate;


	@PostConstruct
	public void kafkaSubscribe() {
		kafkaTemplate.receiveAutoAck().flatMap(record ->
				{
					UserDTO value = record.value();
					FeedUser feedUser = FeedUser.builder()
							.firstname(value.getFirstName())
							.lastname(value.getLastName())
							.photo(value.getProfile())
							.id(value.getId())
							.build();

					return userService.save(feedUser);
				}
		).log().subscribe();
	}


	public Mono<ResponseEntity<UserResponse>> getUserById(String id, ServerWebExchange exchange) {
		return userService.getById(id)
				.map(user -> ResponseEntity.ok(UserResponse.builder()
						.id(user.getId())
						.firstname(user.getFirstname())
						.lastname(user.getLastname())
						.photo(user.getPhoto())
						.build()
				)).log().defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
