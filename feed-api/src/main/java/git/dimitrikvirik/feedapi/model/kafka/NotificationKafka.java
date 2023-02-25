package git.dimitrikvirik.feedapi.model.kafka;

import git.dimitrikvirik.feedapi.model.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationKafka {

	@Id
	private String id;

	private String sourceResourceId;

	private Boolean seen;

	private NotificationType type;

	private String senderUserId;

	private String receiverUserId;


	private ZonedDateTime createdAt;


}
