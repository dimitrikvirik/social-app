package git.dimitrikvirik.feedapi.model.domain;

import git.dimitrikvirik.feedapi.model.enums.ReactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "feed_reaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedReaction implements UserResource {

	@Id
	private String id;


	private String userId;


	private String postId;

	private ReactionType reactionType;
}
