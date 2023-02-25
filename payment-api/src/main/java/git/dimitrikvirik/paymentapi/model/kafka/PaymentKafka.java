package git.dimitrikvirik.paymentapi.model.kafka;

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
