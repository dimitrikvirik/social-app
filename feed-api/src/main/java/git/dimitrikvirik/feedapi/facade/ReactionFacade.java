package git.dimitrikvirik.feedapi.facade;

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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
							.postId(request.getPostId())
							.userId(userId)
							.reactionType(ReactionType.valueOf(request.getType().name()))
							.build();
					return reactionService.createReaction(feedReaction);
				})
				.zipWhen(feedReaction -> postService.getById(feedReaction.getPostId()).doOnNext(feedPost ->
						feedPost.setRating(feedPost.getRating() + 1)
				).flatMap(postService::save))
				.map(tuple2 -> {
					FeedReaction feedReaction = tuple2.getT1();
					ReactionResponse reactionResponse = new ReactionResponse();
					reactionResponse.setPostId(feedReaction.getPostId());
					reactionResponse.setType(ReactionResponse.TypeEnum.valueOf(feedReaction.getReactionType().name()));
					reactionResponse.setId(feedReaction.getId());
					return new ResponseEntity<>(reactionResponse, HttpStatus.OK);
				});

	}
}
