package git.dimitrikvirik.feedapi.model.domain;

import com.thoughtworks.xstream.converters.time.LocalDateTimeConverter;
import git.dimitrikvirik.feedapi.utils.TimeFormat;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.convert.DatePropertyValueConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
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


	private String title;


	private String content;

	private List<FeedTopic> topics;

	private Integer like;

	private Integer dislike;

	private Integer commentCount;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime updatedAt;

	private Double paymentBoost;

	@Override
	public String getUserId() {
		return feedUser.getId();
	}
}
