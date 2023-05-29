package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.ResponseMapper;
import git.dimitrikvirik.feedapi.mapper.UserMapper;
import git.dimitrikvirik.feedapi.model.FriendDetails;
import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.feedapi.model.enums.FriendshipStatus;
import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import git.dimitrikvirik.feedapi.model.kafka.NotificationKafka;
import git.dimitrikvirik.feedapi.service.FriendsService;
import git.dimitrikvirik.feedapi.service.NotificationService;
import git.dimitrikvirik.feedapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsFacade {

	private final UserService userService;
	private final FriendsService friendsService;
	private final NotificationService notificationService;

	public Mono<ResponseEntity<Void>> addFriend(String userId) {

		List<FriendshipStatus> statuses =
			List.of(FriendshipStatus.ACCEPTED, FriendshipStatus.PENDING, FriendshipStatus.REJECTED);

		Mono<FeedUser> currentUser = userService.currentUser();
		Mono<FeedUser> targetUser = userService.getById(userId)
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));

		return currentUser.zipWith(targetUser)
			.flatMap(t -> friendsService
				.findFriendshipForUsers(t.getT1().getUserId(), t.getT2().getUserId(), statuses)
				.flatMap(it -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists.")))
				.switchIfEmpty(friendsService.createFriendRequest(t.getT1().getUserId(), t.getT2().getUserId()))
				.flatMap(f -> notificationService.sendNotification(createFriendRequestNotification((FeedFriends) f))))
			.then(ResponseMapper.toResponseEntity(HttpStatus.OK));
	}

	public Mono<ResponseEntity<Flux<UserDTO>>> findFriends(Integer page, Integer size) {

		Pageable pageRequest = PageRequest.of(page, size);

		Flux<FriendDetails> friendsDetails = userService.currentUser()
			.flatMapMany(u -> friendsService
				.findActiveFriendsByUserId(u.getId(), pageRequest)
				.map(f -> new FriendDetails(getFriendId(u.getId(), f), f.getStatus()))
			);

		Flux<FeedUser> friendUsers = Flux.from(friendsDetails)
			.map(FriendDetails::getUserId)
			.collectList()
			.flatMapMany(userService::findAll);

		Flux<UserDTO> response = friendsDetails.flatMap(fd -> friendUsers
			.filter(fu -> fd.getUserId().equals(fu.getId()))
			.map(fu -> UserMapper.toUserDTO(fu, fd.getStatus())));

		return ResponseMapper.toResponseEntity(HttpStatus.OK, response);
	}

	private String getFriendId(String userId, FeedFriends feedFriends) {
		if (userId.equals(feedFriends.getUserTwoId())) {
			return feedFriends.getUserOneId();
		} else {
			return feedFriends.getUserTwoId();
		}
	}

	private NotificationKafka createFriendRequestNotification(FeedFriends friends) {
		return NotificationKafka.builder()
			.id(UUID.randomUUID().toString())
			.type(NotificationType.FRIEND_REQUEST)
			.seen(false)
			.sourceResourceId(friends.getId())
			.senderUserId(friends.getUserOneId())
			.receiverUserId(friends.getUserTwoId())
			.createdAt(friends.getCreatedAt())
			.build();

	}

}
