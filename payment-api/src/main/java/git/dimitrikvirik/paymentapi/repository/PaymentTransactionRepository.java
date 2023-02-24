package git.dimitrikvirik.paymentapi.repository;

import git.dimitrikvirik.paymentapi.model.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
}
