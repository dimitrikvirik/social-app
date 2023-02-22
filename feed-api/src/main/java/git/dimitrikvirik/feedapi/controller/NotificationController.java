package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.generated.feedapi.api.NotificationApi;
import git.dimitrikvirik.generated.feedapi.model.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class NotificationController implements NotificationApi {

	@Override
	public Mono<ResponseEntity<Flux<NotificationResponse>>> getNotifications(Integer page, Integer size, ServerWebExchange exchange) {
		return NotificationApi.super.getNotifications(page, size, exchange);
	}
}
