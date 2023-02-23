package git.dimitrikvirik.feedapi.model.kafka;

import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

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

	private FeedUser senderUser;

	private String receiverUserId;


	private ZonedDateTime createdAt;


}
