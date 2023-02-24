package git.dimitrikvirik.paymentapi.service;

import git.dimitrikvirik.paymentapi.model.domain.Balance;
import git.dimitrikvirik.paymentapi.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Transactional
public class BalanceService {

	private final BalanceRepository balanceRepository;


	public Balance save(Balance balance) {
		return balanceRepository.save(balance);
	}

	public Balance findByUserId(String userId) {
		return balanceRepository.findByUserId(userId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Balance not found for user with id: " + userId)
		);
	}
}
