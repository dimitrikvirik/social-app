package git.dimitrikvirik.feedapi.model.domain;

import git.dimitrikvirik.feedapi.model.enums.ReactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

@Document(indexName = "feed_reaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedReaction implements UserResource {

	@Id
	private String id;


	private FeedUser feedUser;


	private String postId;

	private ReactionType reactionType;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime updatedAt;

	@Override
	public String getUserId() {
		return feedUser.getId();
	}
}
