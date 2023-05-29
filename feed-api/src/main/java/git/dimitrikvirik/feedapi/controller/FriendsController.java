package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.FriendsFacade;
import git.dimitrikvirik.feedapi.model.dto.FriendRequestDTO;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.generated.feedapi.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

	@Operation(
		description = "Get friends of current user",
		responses = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			})
		}
	)
	@GetMapping
	public Mono<ResponseEntity<Flux<UserDTO>>> findFriends(
		@RequestParam(defaultValue = "0", required = false) Integer page,
		@RequestParam(defaultValue = "10", required = false) Integer size) {

		return facade.findFriends(page, size);
	}

	@Operation(
		description = "Add friend",
		responses = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "409", description = "Conflict, users are already friends", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			})
		}
	)
	@PostMapping("/{userId}")
	public Mono<ResponseEntity<Void>> addFriend(@PathVariable String userId) {
		return facade.addFriend(userId);
	}

	@Operation(
		description = "Remove friend",
		responses = {
			@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			})
		}
	)
	@DeleteMapping("/{userId}")
	public Mono<ResponseEntity<Void>> removeFriend(@PathVariable String userId) {
		return facade.removeFriend(userId);
	}

	@Operation(
		description = "Remove friend",
		responses = {
			@ApiResponse(responseCode = "200", description = "Ok", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = FriendRequestDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			})
		}
	)
	@GetMapping("/requests")
	public Mono<ResponseEntity<Flux<FriendRequestDTO>>> findFriendRequests(
		@RequestParam(defaultValue = "0", required = false) Integer page,
		@RequestParam(defaultValue = "10", required = false) Integer size) {

		return facade.findFriendRequests(page, size);
	}

	@Operation(
		description = "Remove friend",
		responses = {
			@ApiResponse(responseCode = "200", description = "Ok", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "404", description = "Not Found", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			}),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			})
		}
	)
	@PutMapping("/requests/{requestId}")
	public Mono<ResponseEntity<Void>> updateFriendRequest(@PathVariable String requestId, @RequestParam boolean accepted) {
		return facade.updateFriendRequest(requestId, accepted);
	}

}
