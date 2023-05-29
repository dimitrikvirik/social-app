package git.dimitrikvirik.feedapi.facade;

import static git.dimitrikvirik.feedapi.model.enums.FriendshipStatus.*;

import git.dimitrikvirik.feedapi.mapper.FriendsMapper;
import git.dimitrikvirik.feedapi.mapper.ResponseMapper;
import git.dimitrikvirik.feedapi.mapper.UserMapper;
import git.dimitrikvirik.feedapi.model.FriendDetails;
import git.dimitrikvirik.feedapi.model.FriendRequestDetails;
import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.FriendRequestDTO;
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

		List<FriendshipStatus> statuses = List.of(ACCEPTED, PENDING, REJECTED);

		Mono<FeedUser> currentUser = userService.currentUser();
		Mono<FeedUser> targetUser = userService.getById(userId)
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));

		return currentUser.zipWith(targetUser)
			.flatMap(t -> friendsService
				.findFriendshipForUsers(t.getT1().getUserId(), t.getT2().getUserId(), statuses)
				.flatMap(it -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists.")))
				.switchIfEmpty(friendsService.createFriendRequest(t.getT1().getUserId(), t.getT2().getUserId()))
				.flatMap(f -> notificationService.sendNotification(createNewRequestNotification((FeedFriends) f))))
			.then(ResponseMapper.toResponseEntity(HttpStatus.CREATED));
	}

	public Mono<ResponseEntity<Flux<UserDTO>>> findFriends(Integer page, Integer size) {

		Pageable pageRequest = PageRequest.of(page, size);

		Flux<FriendDetails> friendsDetails = userService.currentUser()
			.flatMapMany(u -> friendsService
				.findFriendshipsByUser(u.getId(), List.of(ACCEPTED), pageRequest)
				.map(f -> new FriendDetails(getFriendId(u, f), f.getStatus()))
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

	public Mono<ResponseEntity<Flux<FriendRequestDTO>>> findFriendRequests(Integer page, Integer size) {

		Pageable pageRequest = PageRequest.of(page, size);

		Flux<FriendRequestDetails> requestDetails = userService.currentUser()
			.flatMapMany(u -> friendsService
				.findFriendshipsByUser(u.getId(), List.of(PENDING), pageRequest)
				.map(f -> processFriendRequest(u, f))
			);

		Flux<FeedUser> friendUsers = Flux.from(requestDetails)
			.map(FriendRequestDetails::getUserId)
			.collectList()
			.flatMapMany(userService::findAll);

		Flux<FriendRequestDTO> response = requestDetails.flatMap(rd -> friendUsers
			.filter(fu -> rd.getUserId().equals(fu.getId()))
			.map(fu -> FriendsMapper.toFriendRequestDTO(fu, rd)));

		return ResponseMapper.toResponseEntity(HttpStatus.OK, response);
	}

	public Mono<ResponseEntity<Void>> updateFriendRequest(String requestId, boolean accepted) {
		return userService.currentUser()
			.flatMap(u -> friendsService.findIncomingRequest(requestId, u.getId(), List.of(PENDING)))
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found.")))
			.flatMap(r -> friendsService.updateFriendship(r, accepted ? ACCEPTED : REJECTED))
			.flatMap(r -> notificationService.sendNotification(createUpdatedRequestNotification(r, accepted)))
			.then(ResponseMapper.toResponseEntity(HttpStatus.OK));
	}

	public Mono<ResponseEntity<Void>> removeFriend(String userId) {
		return userService.currentUser()
			.flatMap(u -> friendsService.findFriendshipForUsers(u.getId(), userId, List.of(ACCEPTED)))
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found.")))
			.flatMap(f -> friendsService.updateFriendship(f, DELETED))
			.then(ResponseMapper.toResponseEntity(HttpStatus.NO_CONTENT));
	}

	private String getFriendId(FeedUser currentUser, FeedFriends feedFriends) {
		if (currentUser.getId().equals(feedFriends.getUserTwoId())) {
			return feedFriends.getUserOneId();
		} else {
			return feedFriends.getUserTwoId();
		}
	}

	private FriendRequestDetails processFriendRequest(FeedUser currentUser, FeedFriends request) {
		boolean isReceiver = currentUser.getId().equals(request.getUserTwoId());
		return new FriendRequestDetails(request.getId(),  getFriendId(currentUser, request), isReceiver);
	}

	private NotificationKafka createNewRequestNotification(FeedFriends friends) {
		return NotificationKafka.builder()
			.id(UUID.randomUUID().toString())
			.type(NotificationType.FRIEND_REQUEST_RECEIVED)
			.seen(false)
			.sourceResourceId(friends.getId())
			.senderUserId(friends.getUserOneId())
			.receiverUserId(friends.getUserTwoId())
			.createdAt(friends.getCreatedAt())
			.build();
	}

	private NotificationKafka createUpdatedRequestNotification(FeedFriends friends, boolean accepted) {
		return NotificationKafka.builder()
			.id(UUID.randomUUID().toString())
			.type(accepted ? NotificationType.FRIEND_REQUEST_ACCEPTED : NotificationType.FRIEND_REQUEST_REJECTED)
			.seen(false)
			.sourceResourceId(friends.getId())
			.senderUserId(friends.getUserTwoId())
			.receiverUserId(friends.getUserOneId())
			.createdAt(friends.getCreatedAt())
			.build();
	}

}
