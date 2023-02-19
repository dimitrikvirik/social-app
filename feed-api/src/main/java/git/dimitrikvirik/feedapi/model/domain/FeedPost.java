package git.dimitrikvirik.feedapi.model.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Document(indexName = "feed_post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedPost {
	@Id
	private String id = UUID.randomUUID().toString();

	private User user;

	@CompletionField
	private String title;

	@CompletionField
	private String content;

}
