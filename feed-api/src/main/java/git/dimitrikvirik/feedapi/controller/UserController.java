package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.UserFacade;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.dto.UserDTO;
import git.dimitrikvirik.generated.feedapi.model.ErrorResponse;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserFacade userFacade;

	@Operation(
		description = "Get user by id",
		tags = { "User" },
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
	@GetMapping("/user/{id}")
	public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable String id, ServerWebExchange exchange) {
		return userFacade.getUserById(id, exchange);
	}

	@Operation(
		tags = { "User" }
	)
	@PostMapping("/user")
	public Mono<ResponseEntity<UserResponse>> createUser(@RequestBody FeedUser user) {
		return userFacade.createUser(user);
	}

}



