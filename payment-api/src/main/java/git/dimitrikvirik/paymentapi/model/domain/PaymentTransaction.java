package git.dimitrikvirik.paymentapi.model.domain;

import git.dimitrikvirik.paymentapi.model.enums.TransactionType;
import git.dimitrikvirik.paymentapi.model.kafka.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PaymentTransaction {

	@Id
	private String id;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	@JoinColumn(name = "balance_id")
	@ManyToOne
	private Balance balance;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PaymentStatus status;

	@Column(name = "reason")
	private String reason;

	@CreationTimestamp
	private LocalDateTime createdAt = LocalDateTime.now();
}
