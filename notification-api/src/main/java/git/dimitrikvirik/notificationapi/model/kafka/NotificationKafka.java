package git.dimitrikvirik.notificationapi.model.kafka;

import lombok.*;
import org.apache.kafka.common.protocol.types.Field;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationKafka {

	private String senderUserId;

	private String receiverUserId;

	private String sourceResourceId;

	private String type;
}
