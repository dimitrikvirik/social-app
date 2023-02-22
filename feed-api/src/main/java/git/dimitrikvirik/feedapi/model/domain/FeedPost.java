package git.dimitrikvirik.feedapi.model.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "feed_post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedPost implements UserResource {
	@Id
	private String id;

	private FeedUser feedUser;

	@CompletionField
	private String title;

	@CompletionField
	private String content;

	private List<FeedTopic> topics;

	private Integer like = 0;

	private Integer dislike = 0;

	private Integer commentCount = 0;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
	private LocalDateTime createdAt;

	@Override
	public String getUserId() {
		return feedUser.getId();
	}
}
