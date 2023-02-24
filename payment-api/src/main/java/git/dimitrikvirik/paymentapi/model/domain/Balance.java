package git.dimitrikvirik.paymentapi.model.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "balance")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Balance {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "amount")
	private Double amount;

	@OneToMany(mappedBy = "balance")
	private List<PaymentTransaction> transactions;

	@CreationTimestamp
	private LocalDateTime createdAt;


}
