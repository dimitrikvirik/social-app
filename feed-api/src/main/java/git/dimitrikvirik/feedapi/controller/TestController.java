package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.generated.feedapi.api.TestApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import git.dimitrikvirik.generated.feedapi.model.Test200Response;

@RestController
public class TestController implements TestApi {

	@Override
	public Mono<ResponseEntity<Test200Response>> test(ServerWebExchange exchange) {


		return Mono.just(ResponseEntity.ok(new Test200Response("test")));
	}
}
