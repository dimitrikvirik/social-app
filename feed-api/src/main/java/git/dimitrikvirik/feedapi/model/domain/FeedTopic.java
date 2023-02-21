package git.dimitrikvirik.feedapi.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "feed_topic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedTopic {

	@Id
	private String id;

	@CompletionField
	private String name;

}
