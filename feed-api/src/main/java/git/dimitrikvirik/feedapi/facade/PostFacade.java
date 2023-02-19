package git.dimitrikvirik.feedapi.facade;


import git.dimitrikvirik.feedapi.UserHelper;
import git.dimitrikvirik.feedapi.mapper.PostMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.PostPageResponse;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostFacade {

	private final PostService postService;

	private final UserService userService;


	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postRequest.flatMap(post ->
				UserHelper.currentUserId().flatMap(userService::userById).flatMap(user -> {
					FeedPost feedPost = FeedPost.builder()
							.id(UUID.randomUUID().toString())
							.title(post.getTitle())
							.content(post.getContent())
							.topics(post.getTopics())
							.user(user)
							.build();
					return postService.save(feedPost);
				})).map(PostMapper::toPostResponseEntity);

	}

	public Mono<ResponseEntity<PostResponse>> deletePost(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(post -> {
					postService.deleteById(id);
					return PostMapper.toPostResponseEntity(post);
				}).defaultIfEmpty(ResponseEntity.notFound().build());

	}


	public Mono<ResponseEntity<PostResponse>> getPostById(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(PostMapper::toPostResponseEntity)
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	public Mono<ResponseEntity<PostResponse>> updatePost(String id, Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postRequest.flatMap(post -> {
			FeedPost feedPost = FeedPost.builder()
					.title(post.getTitle())
					.content(post.getContent())
					.build();
			return postService.save(feedPost);
		}).map(PostMapper::toPostResponseEntity);
	}

	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		Flux<PostResponse> postResponseFlux = postService.getAll(page, size, searchText).map(PostMapper::toPostResponse);

		return Mono.just(ResponseEntity.ok(postResponseFlux));

	}


}
