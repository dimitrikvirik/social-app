package git.dimitrikvirik.paymentapi.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.common.util.UserHelper;
import git.dimitrikvirik.payment.model.TransactionResponse;
import git.dimitrikvirik.paymentapi.mapper.TransactionMapper;
import git.dimitrikvirik.paymentapi.model.domain.PaymentTransaction;
import git.dimitrikvirik.paymentapi.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionFacade {

	private final PaymentTransactionService service;



	public TransactionResponse getTransaction(String id) {
		PaymentTransaction transaction = service.findById(id);
		if (!transaction.getBalance().getUserId().equals(UserHelper.currentUserId()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this transaction");
		return TransactionMapper.map(transaction);
	}

	public List<TransactionResponse> getTransactions(Integer size, Integer page) {
		return service.findAll(size, page, UserHelper.currentUserId()).stream().map(TransactionMapper::map).toList();
	}
}
