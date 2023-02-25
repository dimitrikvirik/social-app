package git.dimitrikvirik.feedapi.model.kafka;

import git.dimitrikvirik.feedapi.model.enums.PaymentStatus;
import git.dimitrikvirik.feedapi.model.enums.PaymentType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentKafka {

	private String transactionId;

	private String resourceId;

	private PaymentType type;

	private PaymentStatus status;

	private String reason;

}
