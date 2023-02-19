package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.PostFacade;
import git.dimitrikvirik.feedapi.model.domain.User;
import git.dimitrikvirik.generated.feedapi.api.PostApi;
import git.dimitrikvirik.generated.feedapi.model.PostPageResponse;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Collector;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class FeedPostController implements PostApi {

	private final PostFacade postFacade;

	@Override
	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postFacade.createPost(postRequest, exchange);
	}

	@Override
	public Mono<ResponseEntity<PostResponse>> deletePost(String id, ServerWebExchange exchange) {
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
	@GetMapping(value = "/test",  produces =  MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<ResponseEntity<Flux<UserResponse>>> test() {

		var flux = Flux.fromStream(
				Stream.of(
						UserResponse.builder().id("1").firstname("name1").build(),
						UserResponse.builder().id("2").firstname("name2").build(),
						UserResponse.builder().id("3").firstname("name3").build()
				)
		).delayElements(Duration.ofSeconds(2));


		return Mono.just(ResponseEntity.ok(flux));


	}

}
