package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.CommentFacade;
import git.dimitrikvirik.generated.feedapi.api.CommentApi;
import git.dimitrikvirik.generated.feedapi.model.CommentRequest;
import git.dimitrikvirik.generated.feedapi.model.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class CommentController implements CommentApi {

	private final CommentFacade commentFacade;

	@Override
	public Mono<ResponseEntity<CommentResponse>> createComment(Mono<CommentRequest> commentRequest, ServerWebExchange exchange) {
		return commentFacade.createComment(commentRequest, exchange);
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteComment(String id, ServerWebExchange exchange) {
		return commentFacade.deleteComment(id, exchange);
	}

	@Override
	public Mono<ResponseEntity<Flux<CommentResponse>>> getAllComments(Integer page, Integer size, String postId, ServerWebExchange exchange) {
		return commentFacade.getAllComments(page, size, postId, exchange);
	}

	@Override
	public Mono<ResponseEntity<CommentResponse>> getCommentById(String id, ServerWebExchange exchange) {
		return commentFacade.getCommentById(id, exchange);
	}

	@Override
	public Mono<ResponseEntity<CommentResponse>> updateComment(String id, Mono<CommentRequest> commentRequest, ServerWebExchange exchange) {
		return commentFacade.updateComment(id, commentRequest, exchange);
	}
}
