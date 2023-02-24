package git.dimitrikvirik.paymentapi.controller;

import git.dimitrikvirik.payment.api.BalanceApi;
import git.dimitrikvirik.payment.model.*;
import git.dimitrikvirik.paymentapi.facade.BalanceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BalanceController implements BalanceApi {

	private final BalanceFacade balanceFacade;

	@Override
	public ResponseEntity<TransactionResponse> chargeBalance(ChargeBalanceRequest chargeBalanceRequest) {
		return ResponseEntity.ok(balanceFacade.chargeBalance(chargeBalanceRequest));
	}

	@Override
	public ResponseEntity<BalanceResponse> getBalance() {
		return ResponseEntity.ok(balanceFacade.getBalance());
	}

	@Override
	public ResponseEntity<TransactionResponse> refundBalance(RefundBalanceRequest refundBalanceRequest) {
		return ResponseEntity.ok(balanceFacade.refundBalance(refundBalanceRequest));
	}

	@Override
	public ResponseEntity<TransactionResponse> testFillBalance(TestFillBalanceRequest testFillBalanceRequest) {
		return ResponseEntity.ok(balanceFacade.testFillBalance(testFillBalanceRequest));
	}
}
