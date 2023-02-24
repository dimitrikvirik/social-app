package git.dimitrikvirik.paymentapi.repository;

import git.dimitrikvirik.paymentapi.model.domain.Balance;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, String> {

	@Lock(value = jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value ="10000")})
	Optional<Balance> findByUserId(String userId);

}
