package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.ReactionMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.feedapi.model.enums.ReactionType;
import git.dimitrikvirik.feedapi.service.PostService;
import git.dimitrikvirik.feedapi.service.ReactionService;
import git.dimitrikvirik.feedapi.utils.UserHelper;
import git.dimitrikvirik.generated.feedapi.model.ReactionRequest;
import git.dimitrikvirik.generated.feedapi.model.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionFacade {

	private final ReactionService reactionService;

	private final PostService postService;

	public Mono<ResponseEntity<ReactionResponse>> createReaction(Mono<ReactionRequest> reactionRequest, ServerWebExchange exchange) {


		return reactionRequest
				.zipWith(UserHelper.currentUserId())
				.zipWhen(tuple2 -> {
					ReactionRequest request = tuple2.getT1();
					String userId = tuple2.getT2();

					return reactionService.findByPost(request.getPostId(), userId);
				})
				.flatMap(tuple2 -> {
					Boolean hasReaction = tuple2.getT2();
					ReactionRequest request = tuple2.getT1().getT1();
					String userId = tuple2.getT1().getT2();
					if (hasReaction) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has reaction"));
					}
					FeedReaction feedReaction = FeedReaction.builder()
							.id(UUID.randomUUID().toString())
							.createdAt(ZonedDateTime.now())
							.postId(request.getPostId())
							.userId(userId)
							.reactionType(ReactionType.valueOf(request.getType().name()))
							.build();
					return reactionService.save(feedReaction);
				})
				//TODO notification
				.zipWhen(feedReaction -> postService.getById(feedReaction.getPostId()).doOnNext(feedPost -> {
							if (feedReaction.getReactionType().equals(ReactionType.LIKE)) {
								feedPost.setLike(feedPost.getLike() + 1);
							} else if (feedReaction.getReactionType().equals(ReactionType.DISLIKE)) {
								feedPost.setDislike(feedPost.getDislike() + 1);
							}
						}
				).flatMap(postService::save))
				.map(tuple2 -> ReactionMapper.toReactionResponseEntityCreated(tuple2.getT1()));

	}

	public Mono<ResponseEntity<Flux<ReactionResponse>>> getAllReactions(Integer page, Integer size, ServerWebExchange exchange) {
		Flux<ReactionResponse> responseFlux = UserHelper.currentUserId().flatMapMany(userId -> reactionService.getAllReactions(userId, page, size))
				.map(ReactionMapper::toReactionResponse);
		return Mono.just(new ResponseEntity<>(responseFlux, HttpStatus.OK));
	}

	public Mono<ResponseEntity<Void>> deleteReaction(String id, ServerWebExchange exchange) {
		return reactionService.getByIdValidated(id).flatMap(reactionService::delete).then(Mono.just(ResponseEntity.noContent().build()));
	}

	public Mono<ResponseEntity<ReactionResponse>> getReactionById(String id, ServerWebExchange exchange) {
		return reactionService.getById(id).map(ReactionMapper::toReactionResponseEntityOk);
	}

	public Mono<ResponseEntity<ReactionResponse>> updateReaction(String id, Mono<ReactionRequest> reactionRequest, ServerWebExchange exchange) {
		return reactionRequest.zipWith(reactionService.getByIdValidated(id))
				.flatMap(tuple2 -> {
					ReactionRequest request = tuple2.getT1();
					FeedReaction feedReaction = tuple2.getT2();
					feedReaction.setUpdatedAt(ZonedDateTime.now());
					feedReaction.setReactionType(ReactionType.valueOf(request.getType().name()));
					return reactionService.save(feedReaction);
				}).map(ReactionMapper::toReactionResponseEntityOk);
	}


}
