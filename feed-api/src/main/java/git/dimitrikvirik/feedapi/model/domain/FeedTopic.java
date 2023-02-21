package git.dimitrikvirik.feedapi.model.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "feed_topic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedTopic {

	@Id
	private String id;

	@CompletionField
	private String name;

}
