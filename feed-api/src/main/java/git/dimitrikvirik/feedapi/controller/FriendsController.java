package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.FriendsFacade;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friends")
public class FriendsController {

	private final FriendsFacade facade;

	@GetMapping
	public Mono<ResponseEntity<Flux<UserDTO>>> findFriends(
		@RequestParam(defaultValue = "0", required = false) Integer page,
		@RequestParam(defaultValue = "10", required = false) Integer size) {

		return facade.findFriends(page, size);
	}

	@PostMapping("/{userId}")
	public Mono<ResponseEntity<Void>> addFriend(@PathVariable String userId) {
		return facade.addFriend(userId);
	}

}
