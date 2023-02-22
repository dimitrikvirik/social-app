package git.dimitrikvirik.feedapi.model.domain;

import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

@Document(indexName = "feed_notification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FeedNotification {

	@Id
	private String id;

	private String sourceResourceId;

	private Boolean seen;

	private NotificationType type;


	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime createdAt;


}
