package git.dimitrikvirik.paymentapi.controller;

import git.dimitrikvirik.payment.api.TransactionApi;
import git.dimitrikvirik.payment.model.TransactionResponse;
import git.dimitrikvirik.paymentapi.facade.TransactionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {

	private final TransactionFacade facade;

	@Override
	public ResponseEntity<TransactionResponse> getTransaction(String id) {
		return ResponseEntity.ok(facade.getTransaction(id));
	}

	@Override
	public ResponseEntity<List<TransactionResponse>> getTransactions(Integer size, Integer page) {
		return ResponseEntity.ok(facade.getTransactions(size, page));
	}
}
