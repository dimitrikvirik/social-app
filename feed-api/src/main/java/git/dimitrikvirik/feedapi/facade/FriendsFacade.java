package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.ResponseMapper;
import git.dimitrikvirik.feedapi.mapper.UserMapper;
import git.dimitrikvirik.feedapi.model.FriendDetails;
import git.dimitrikvirik.feedapi.model.domain.FeedFriends;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.feedapi.service.FriendsService;
import git.dimitrikvirik.feedapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsFacade {

	private final UserService userService;
	private final FriendsService friendsService;

	public Mono<ResponseEntity<Void>> addFriend(String userId) {
		return userService.currentUser()
			.flatMap(u -> friendsService.addFriend(u.getId(), userId))
			.then(ResponseMapper.toResponseEntity(HttpStatus.CREATED));
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

}
