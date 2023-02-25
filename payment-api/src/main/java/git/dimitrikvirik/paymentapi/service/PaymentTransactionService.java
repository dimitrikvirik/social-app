package git.dimitrikvirik.paymentapi.service;

import git.dimitrikvirik.paymentapi.model.domain.PaymentTransaction;
import git.dimitrikvirik.paymentapi.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTransactionService {
	private final PaymentTransactionRepository repository;

	public PaymentTransaction save(PaymentTransaction paymentTransaction) {
		return repository.save(paymentTransaction);
	}

	public PaymentTransaction findById(String id) {
		return repository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment transaction not found with id: " + id)
		);
	}

	public List<PaymentTransaction> findAll(int size, int page, String userId) {
		return repository.findAllByBalanceUserId(userId, PageRequest.of(page, size)).getContent();
	}

}
