package git.dimitrikvirik.feedapi.facade;


import git.dimitrikvirik.feedapi.mapper.PostMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.service.*;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import git.dimitrikvirik.generated.feedapi.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFacade {

	private final PostService postService;

	private final UserService userService;

	private final TopicService topicService;

	private final ReactionService reactionService;

	private final CommentService commentService;


	public Mono<ResponseEntity<PostResponse>> createPost(Mono<PostRequest> postRequest, ServerWebExchange exchange) {

		return postRequest
				.zipWhen(post -> topicService.findAllByIds(post.getTopics()).collectList())
				.zipWith(userService.currentUser())
				.map(tuple -> {
					FeedUser feedUser = tuple.getT2();
					List<FeedTopic> topics = tuple.getT1().getT2();
					PostRequest request = tuple.getT1().getT1();
					return FeedPost.builder()
							.id(UUID.randomUUID().toString())
							.title(request.getTitle())
							.like(0)
							.dislike(0)
							.commentCount(0)
							.content(request.getContent())
							.topics(topics)
							.feedUser(feedUser)
							.createdAt(ZonedDateTime.now())
							.updatedAt(ZonedDateTime.now())
							.build();
				}).flatMap(postService::save).map(PostMapper::toPostResponseEntityCreated);
	}


	public Mono<ResponseEntity<Void>> deletePost(String id, ServerWebExchange exchange) {
		return postService.getByIdValidated(id)
				.flatMap(postService::delete)
				.then(reactionService.deleteAllByPostId(id))
				.then(commentService.deleteAllByPostId(id))
				.then(Mono.just(ResponseEntity.noContent().build()));
	}


	public Mono<ResponseEntity<PostResponse>> getPostById(String id, ServerWebExchange exchange) {
		return postService.getById(id)
				.map(PostMapper::toPostResponseEntityOk)
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	public Mono<ResponseEntity<PostResponse>> updatePost(String id, Mono<PostRequest> postRequest, ServerWebExchange exchange) {

		return postRequest.zipWhen(post -> topicService.findAllByIds(post.getTopics()).collectList()).zipWith(
				postService.getByIdValidated(id)
		).flatMap(
				tuple -> {
					PostRequest request = tuple.getT1().getT1();
					FeedPost feedPost = tuple.getT2();
					feedPost.setTitle(request.getTitle());
					feedPost.setContent(request.getContent());
					feedPost.setTopics(tuple.getT1().getT2());
					feedPost.setUpdatedAt(ZonedDateTime.now());
					return postService.save(feedPost);
				}
		).map(PostMapper::toPostResponseEntityOk);


	}

	public Mono<ResponseEntity<Flux<PostResponse>>> getAllPosts(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok().body(postService.getAll(page, size, searchText).map(PostMapper::toPostResponse)));
	}


}
