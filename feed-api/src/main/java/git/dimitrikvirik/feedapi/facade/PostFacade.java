package git.dimitrikvirik.feedapi.facade;


import git.dimitrikvirik.feedapi.mapper.PostMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.TopicService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.feedapi.utils.UserHelper;
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
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFacade {

	private final PostService postService;

	private final UserService userService;

	private final TopicService topicService;


	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {

		Mono<Tuple2<PostRequest, List<FeedTopic>>> postRequestTuple = postRequest.zipWhen(post ->
				topicService.findAllByIds(post.getTopics()).switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"))).collectList());


		Mono<FeedUser> feedUserMono = UserHelper.currentUserId().flatMap(userService::userById);

		return Mono.zip(postRequestTuple, feedUserMono).flatMap(tuple -> {
			PostRequest request = tuple.getT1().getT1();
			List<FeedTopic> topics = tuple.getT1().getT2();
			FeedUser feedUser = tuple.getT2();


			FeedPost feedPost = FeedPost.builder()
					.id(UUID.randomUUID().toString())
					.title(request.getTitle())
					.content(request.getContent())
					.topics(topics)
					.feedUser(feedUser)
					.createdAt(LocalDateTime.now())
					.build();
			return postService.save(feedPost);
		}).map(PostMapper::toPostResponseEntityCreated);
	}

	public Mono<ResponseEntity<Void>> deletePost(String id, ServerWebExchange exchange) {
		return postService.getById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")))
				.flatMap(postService::delete).map(PostMapper::toPostResponseEntityNoContent);
	}


	public Mono<ResponseEntity<PostResponse>> getPostById(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(PostMapper::toPostResponseEntityOk)
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
			return postService.save(feedPost).map(PostMapper::toPostResponseEntityOk);
		});

	}

	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok().body(postService.getAll(page, size, searchText).map(PostMapper::toPostResponse)));
	}


}
