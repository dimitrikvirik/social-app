package git.dimitrikvirik.paymentapi.model.domain;

import git.dimitrikvirik.paymentapi.model.enums.TransactionType;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	@JoinColumn(name = "balance_id")
	@ManyToOne
	private Balance balance;

	@CreationTimestamp
	private LocalDateTime createdAt = LocalDateTime.now();
}
