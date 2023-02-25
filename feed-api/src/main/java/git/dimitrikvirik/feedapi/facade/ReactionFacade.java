package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.ReactionMapper;
import git.dimitrikvirik.feedapi.model.kafka.NotificationKafka;
import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import git.dimitrikvirik.feedapi.model.enums.ReactionType;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.ReactionService;
import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.ReactionRequest;
import git.dimitrikvirik.generated.feedapi.model.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionFacade {

	private final ReactionService reactionService;

	private final PostService postService;

	private final UserService userService;

	private final ReactiveKafkaProducerTemplate<String, NotificationKafka> kafkaTemplate;


	public Mono<ResponseEntity<ReactionResponse>> createReaction(Mono<ReactionRequest> reactionRequest, ServerWebExchange exchange) {


		return reactionRequest
				.zipWith(userService.currentUser())
				.zipWhen(tuple2 -> {
					ReactionRequest request = tuple2.getT1();
					FeedUser user = tuple2.getT2();

					return reactionService.findByPost(request.getPostId(), user.getUserId()).zipWith(
							postService.getById(request.getPostId())
					);
				})
				.flatMap(tuple2 -> {
					String reactionId = UUID.randomUUID().toString();
					ReactionRequest request = tuple2.getT1().getT1();
					FeedUser feedUser = tuple2.getT1().getT2();
					Boolean hasReaction = tuple2.getT2().getT1();
					FeedPost feedPost = tuple2.getT2().getT2();
					Mono<SenderResult<Void>> senderResultMono;
					if (!feedPost.getFeedUser().getUserId().equals(feedUser.getId()))
						senderResultMono = kafkaTemplate.send("notification", feedPost.getUserId(), NotificationKafka.builder()
								.id(UUID.randomUUID().toString())
								.seen(false)
								.sourceResourceId(reactionId)
								.createdAt(ZonedDateTime.now())
								.senderUserId(feedUser.getUserId())
								.receiverUserId(feedPost.getUserId())
								.type(NotificationType.REACTION)
								.build()
						);
					else
						senderResultMono = Mono.empty();

					if (hasReaction) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has reaction"));
					}
					if (request.getType().equals(ReactionRequest.TypeEnum.LIKE)) {
						feedPost.setLike(feedPost.getLike() + 1);
					} else if (request.getType().equals(ReactionRequest.TypeEnum.DISLIKE)) {
						feedPost.setDislike(feedPost.getDislike() + 1);
					}


					FeedReaction feedReaction = FeedReaction.builder()
							.id(reactionId)
							.createdAt(ZonedDateTime.now())
							.updatedAt(ZonedDateTime.now())
							.postId(request.getPostId())
							.feedUser(feedUser)
							.reactionType(ReactionType.valueOf(request.getType().name()))
							.build();
					return senderResultMono.then(postService.save(feedPost)).then(reactionService.save(feedReaction));
				})
				.map(ReactionMapper::toReactionResponseEntityCreated);

	}

	public Mono<ResponseEntity<Flux<ReactionResponse>>> getAllReactions(Integer page, Integer size, String postId, ServerWebExchange exchange) {
		Flux<ReactionResponse> responseFlux = reactionService.getAllReactions(page, size, postId).map(ReactionMapper::toReactionResponse);
		return Mono.just(new ResponseEntity<>(responseFlux, HttpStatus.OK));
	}

	public Mono<ResponseEntity<Void>> deleteReaction(String id, ServerWebExchange exchange) {
		return reactionService.getByIdValidated(id)
				.zipWhen(reaction -> postService.getById(reaction.getPostId()))
				.flatMap(tuple2 -> {
					FeedReaction reaction = tuple2.getT1();
					FeedPost feedPost = tuple2.getT2();
					if (reaction.getReactionType().equals(ReactionType.LIKE)) {
						feedPost.setLike(tuple2.getT2().getLike() - 1);
					} else if (reaction.getReactionType().equals(ReactionType.DISLIKE)) {
						feedPost.setDislike(tuple2.getT2().getDislike() - 1);
					}
					return postService.save(feedPost).and(reactionService.delete(reaction));
				})
				.then(Mono.just(ResponseEntity.noContent().build()));
	}

	public Mono<ResponseEntity<ReactionResponse>> getReactionById(String id, ServerWebExchange exchange) {
		return reactionService.getById(id).map(ReactionMapper::toReactionResponseEntityOk);
	}


}
