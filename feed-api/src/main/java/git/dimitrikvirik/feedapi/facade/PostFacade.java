package git.dimitrikvirik.feedapi.facade;


import git.dimitrikvirik.feedapi.UserHelper;
import git.dimitrikvirik.feedapi.mapper.UserMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostFacade {

	private final PostService postService;

	private final UserService userService;


	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postRequest.flatMap(post ->
				UserHelper.currentUserId().flatMap(userService::userById).flatMap(user -> {
					FeedPost feedPost = FeedPost.builder()
							.title(post.getTitle())
							.content(post.getContent())
							.user(user)
							.build();
					return postService.save(feedPost);
				})).map(post ->
				ResponseEntity.ok(PostResponse.builder()
						.title(post.getTitle())
						.id(post.getId())
						.user(UserMapper.fromUser(post.getUser()))
						.content(post.getContent()).build())
		);

	}

	public Mono<ResponseEntity<PostResponse>> deletePost(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(post -> {
					postService.deleteById(id);
					return ResponseEntity.ok(PostResponse.builder()
							.title(post.getTitle())
							.content(post.getContent()).build());
				}).defaultIfEmpty(ResponseEntity.notFound().build());

	}

	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(postService.getAll().map(post ->
				PostResponse.builder()
						.title(post.getTitle())
						.content(post.getContent()).build())));
	}

	public Mono<ResponseEntity<PostResponse>> getPostById(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(post -> ResponseEntity.ok(PostResponse.builder()
						.title(post.getTitle())
						.content(post.getContent()).build()))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	public Mono<ResponseEntity<PostResponse>> updatePost(String id, Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		return postRequest.flatMap(post -> {
			FeedPost feedPost = FeedPost.builder()
					.title(post.getTitle())
					.content(post.getContent())
					.build();
			return postService.save(feedPost);
		}).map(post ->
				ResponseEntity.ok(PostResponse.builder()
						.title(post.getTitle())
						.content(post.getContent()).build())
		);
	}
}
