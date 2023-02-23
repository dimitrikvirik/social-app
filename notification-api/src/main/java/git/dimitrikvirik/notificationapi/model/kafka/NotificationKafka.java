package git.dimitrikvirik.notificationapi.model.kafka;

import org.apache.kafka.common.protocol.types.Field;

public class NotificationKafka {

	private String senderUserId;

	private String receiverUserId;

	private String sourceResourceId;
	private Boolean seen;

	private Field.Str type;
}
