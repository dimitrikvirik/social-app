package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.ReactionFacade;
import git.dimitrikvirik.generated.feedapi.api.ReactionApi;
import git.dimitrikvirik.generated.feedapi.model.ReactionRequest;
import git.dimitrikvirik.generated.feedapi.model.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ReactionController implements ReactionApi {

	private final ReactionFacade reactionFacade;

	@Override
	public Mono<ResponseEntity<ReactionResponse>> createReaction(Mono<ReactionRequest> reactionRequest, ServerWebExchange exchange) {
		return reactionFacade.createReaction(reactionRequest, exchange);
	}
}
