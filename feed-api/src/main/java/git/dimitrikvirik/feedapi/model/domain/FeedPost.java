package git.dimitrikvirik.feedapi.model.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "feed_post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedPost {
	@Id
	private String id;

	private User user;

	@CompletionField
	private String title;

	@CompletionField
	private String content;

	public List<String> topics;

}
