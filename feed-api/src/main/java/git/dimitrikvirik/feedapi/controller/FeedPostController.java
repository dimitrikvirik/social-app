package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.PostFacade;
import git.dimitrikvirik.generated.feedapi.api.PostApi;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class FeedPostController implements PostApi {

	private final PostFacade postFacade;

	@Override
	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postFacade.createPost(postRequest, exchange);
	}

	@Override
	public Mono<ResponseEntity<Void>> deletePost(String id, ServerWebExchange exchange) {
		return postFacade.deletePost(id, exchange);
	}


	@Override
	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		return postFacade.getAllPosts(page, size, searchText, exchange);
	}

	@Override
	public Mono<ResponseEntity<PostResponse>> getPostById(String id, ServerWebExchange exchange) {
		return postFacade.getPostById(id, exchange);
	}

	@Override
	public Mono<ResponseEntity<PostResponse>> updatePost(String id, Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postFacade.updatePost(id, postRequest, exchange);
	}

}
