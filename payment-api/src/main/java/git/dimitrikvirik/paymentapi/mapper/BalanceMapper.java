package git.dimitrikvirik.paymentapi.mapper;

import git.dimitrikvirik.payment.model.BalanceResponse;
import git.dimitrikvirik.paymentapi.model.domain.Balance;

public class BalanceMapper {

	public static BalanceResponse map(Balance balance) {
		return BalanceResponse.builder()
				.balance(balance.getAmount())
				.build();
	}

}
