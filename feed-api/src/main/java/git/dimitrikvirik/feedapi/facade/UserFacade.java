package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;

@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserService userService;

	public Mono<ResponseEntity<UserResponse>> getUserById(String id, ServerWebExchange exchange) {
		return userService.userById(id)
				.map(user -> ResponseEntity.ok(new UserResponse()
						.id(user.getId())
						.firstname(user.getFirstname())
						.lastname(user.getLastname())
						.photo(user.getPhoto())
				)).log().defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
