package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.CommentMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedComment;
import git.dimitrikvirik.feedapi.model.kafka.FeedNotification;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import git.dimitrikvirik.feedapi.service.CommentService;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.CommentRequest;
import git.dimitrikvirik.generated.feedapi.model.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentFacade {

	private final CommentService commentService;

	private final PostService postService;

	private final UserService userService;

	private final ReactiveKafkaProducerTemplate<String, FeedNotification> kafkaTemplate; //TODO

	public Mono<ResponseEntity<CommentResponse>> createComment(Mono<CommentRequest> commentRequest, ServerWebExchange exchange) {
		return commentRequest.zipWhen(comment -> postService.getById(comment.getPostId()))
				.zipWith(userService.currentUser())
				.flatMap(tuple -> {
					FeedUser feedUser = tuple.getT2();
					FeedPost feedPost = tuple.getT1().getT2();
					CommentRequest comment = tuple.getT1().getT1();
					String commentId = UUID.randomUUID().toString();
					//TODO check
					Mono<SenderResult<Void>> senderResultMono = kafkaTemplate.send("feed-notification", feedPost.getUserId(), FeedNotification.builder()
							.id(UUID.randomUUID().toString())
							.seen(false)
							.createdAt(ZonedDateTime.now())
							.senderUser(feedUser)
							.receiverUserId(feedPost.getUserId())
							.sourceResourceId(commentId)
							.type(NotificationType.COMMENT)
							.build()
					).doOnError(result -> {
						System.out.println("Error sending message: " + result);
					}).doOnSuccess(result -> {
						System.out.println("Message sent: " + result);
					});


					FeedComment feedComment = FeedComment
							.builder()
							.id(commentId)
							.feedUser(feedUser)
							.createdAt(ZonedDateTime.now())
							.updatedAt(ZonedDateTime.now())
							.content(comment.getContent())
							.postId(feedPost.getId())
							.build();

					feedPost.setCommentCount(feedPost.getCommentCount() + 1);

					return senderResultMono.then(postService.save(feedPost)).then(commentService.save(feedComment));
				}).map(CommentMapper::toCommentResponseEntityCreated);

	}

	public Mono<ResponseEntity<Void>> deleteComment(String id, ServerWebExchange exchange) {
		return commentService.getByIdValidated(id)
				.zipWhen(comment -> postService.getById(comment.getPostId()))
				.flatMap(tuple -> {
					FeedComment feedComment = tuple.getT1();
					FeedPost feedPost = tuple.getT2();
					feedPost.setCommentCount(feedPost.getCommentCount() - 1);
					return postService.save(feedPost).and(commentService.delete(feedComment));
				}).then(Mono.just(ResponseEntity.noContent().build()));
	}

	public Mono<ResponseEntity<Flux<CommentResponse>>> getAllComments(Integer page, Integer size, String postId, ServerWebExchange exchange) {
		Flux<FeedComment> feedCommentFlux = commentService.getAll(page, size, postId);
		return Mono.just(ResponseEntity.ok(feedCommentFlux.map(CommentMapper::toCommentResponse)));
	}

	public Mono<ResponseEntity<CommentResponse>> getCommentById(String id, ServerWebExchange exchange) {
		return commentService.getById(id).map(CommentMapper::toCommentResponseEntityOk);
	}

	public Mono<ResponseEntity<CommentResponse>> updateComment(String id, Mono<CommentRequest> commentRequest, ServerWebExchange exchange) {
		return commentService.getByIdValidated(id).zipWith(commentRequest).flatMap(tuple -> {
			FeedComment feedComment = tuple.getT1();
			CommentRequest comment = tuple.getT2();
			feedComment.setContent(comment.getContent());
			feedComment.setUpdatedAt(ZonedDateTime.now());
			return commentService.save(feedComment);
		}).map(CommentMapper::toCommentResponseEntityOk);
	}
}
