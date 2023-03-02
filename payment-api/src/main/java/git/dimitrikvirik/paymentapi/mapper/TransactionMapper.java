package git.dimitrikvirik.paymentapi.mapper;

import git.dimitrikvirik.payment.model.TransactionResponse;
import git.dimitrikvirik.paymentapi.model.domain.PaymentTransaction;

public class TransactionMapper {

	public static TransactionResponse map(PaymentTransaction paymentTransaction) {
		return TransactionResponse.builder()
				.id(paymentTransaction.getId())
				.amount(paymentTransaction.getAmount())
				.type(TransactionResponse.TypeEnum.valueOf(paymentTransaction.getType().name()))
				.createdAt(paymentTransaction.getCreatedAt().toString())
				.status(TransactionResponse.StatusEnum.valueOf(paymentTransaction.getStatus().name()))
				.reason(paymentTransaction.getReason())
				.build();
	}
}
