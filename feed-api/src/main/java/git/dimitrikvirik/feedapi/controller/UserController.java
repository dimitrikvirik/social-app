package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.UserFacade;
import git.dimitrikvirik.generated.feedapi.api.UserApi;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

	private final UserFacade userFacade;

	@Override
	public Mono<ResponseEntity<UserResponse>> getUserById(String id, ServerWebExchange exchange) {


		return userFacade.getUserById(id, exchange);
	}
}



