package git.dimitrikvirik.feedapi.model.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;
import java.util.List;

@Document(indexName = "feed_comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedComment implements UserResource {

	@Id
	private String id;

	private String postId;

	private FeedUser feedUser;

	private String content;


	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime updatedAt;

	@Override
	public String getUserId() {
		return feedUser.getId();
	}

}
