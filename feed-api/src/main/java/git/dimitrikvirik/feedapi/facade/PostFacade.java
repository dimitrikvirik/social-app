package git.dimitrikvirik.feedapi.facade;


import git.dimitrikvirik.feedapi.UserHelper;
import git.dimitrikvirik.feedapi.mapper.PostMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.TopicService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

	private final PostService postService;

	private final UserService userService;

	private final TopicService topicService;


	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {
		Flux<FeedTopic> feedTopicFlux = postRequest.flatMapMany(post -> topicService.findAllByIds(post.getTopics()));
		Mono<FeedUser> feedUserMono = UserHelper.currentUserId().flatMap(userService::userById);

		return Mono.zip(feedUserMono, feedTopicFlux.collectList(), postRequest).flatMap(tuple -> {
			if (tuple.getT2().isEmpty())
				return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topics not found"));

			FeedPost feedPost = FeedPost.builder()
					.feedUser(tuple.getT1())
					.title(tuple.getT3().getTitle())
					.content(tuple.getT3().getContent())
					.createdAt(LocalDateTime.now())
					.topics(tuple.getT2())
					.build();
			return postService.save(feedPost).map(PostMapper::toPostResponseEntity);
		});

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
		Mono<String> currentUserId = UserHelper.currentUserId();
		Mono<List<FeedTopic>> feedTopicFlux = postRequest.flatMapMany(post -> topicService.findAllByIds(post.getTopics())).collectList();
		Mono<FeedPost> postMono = postService.getById(id);
		return Mono.zip(currentUserId, feedTopicFlux, postMono).flatMap(post -> {
			String userId = post.getT1();
			List<FeedTopic> feedTopics = post.getT2();
			FeedPost feedPost = post.getT3();

			if (!feedPost.getFeedUser().getId().equals(userId))
				return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't update this post"));

			feedPost.setTitle(feedPost.getTitle());
			feedPost.setContent(feedPost.getContent());
			feedPost.setTopics(feedTopics);
			return postService.save(feedPost).map(PostMapper::toPostResponseEntity);
		});

	}

	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		Flux<PostResponse> postResponseFlux = postService.getAll(page, size, searchText).map(PostMapper::toPostResponse);

		return Mono.just(ResponseEntity.ok(postResponseFlux));

	}


}
