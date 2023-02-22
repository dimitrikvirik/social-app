package git.dimitrikvirik.feedapi.mapper;

import git.dimitrikvirik.feedapi.model.domain.FeedReaction;
import git.dimitrikvirik.generated.feedapi.model.ReactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReactionMapper {


	public static ReactionResponse toReactionResponse(FeedReaction reaction) {
		ReactionResponse reactionResponse = new ReactionResponse();
		reactionResponse.setPostId(reaction.getPostId());
		reactionResponse.setType(ReactionResponse.TypeEnum.valueOf(reaction.getReactionType().name()));
		reactionResponse.setId(reaction.getId());
		return reactionResponse;
	}

	public static ResponseEntity<ReactionResponse> toReactionResponseEntityOk(FeedReaction reaction) {
		return new ResponseEntity<>(toReactionResponse(reaction), HttpStatus.OK);
	}

	public static ResponseEntity<ReactionResponse> toReactionResponseEntityCreated(FeedReaction reaction) {
		return new ResponseEntity<>(toReactionResponse(reaction), HttpStatus.CREATED);
	}

	public static ResponseEntity<Void> toReactionResponseEntityNoContent(Void unused) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
