package git.dimitrikvirik.paymentapi.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.common.util.UserHelper;
import git.dimitrikvirik.payment.model.*;
import git.dimitrikvirik.paymentapi.mapper.BalanceMapper;
import git.dimitrikvirik.paymentapi.mapper.TransactionMapper;
import git.dimitrikvirik.paymentapi.model.domain.Balance;
import git.dimitrikvirik.paymentapi.model.domain.PaymentTransaction;
import git.dimitrikvirik.paymentapi.model.enums.TransactionType;
import git.dimitrikvirik.paymentapi.model.kafka.UserKafka;
import git.dimitrikvirik.paymentapi.service.BalanceService;
import git.dimitrikvirik.paymentapi.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceFacade {

	private final BalanceService balanceService;

	private final PaymentTransactionService transactionService;

	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "user", groupId = "payment-api")
	public void kafkaListener(ConsumerRecord<String, String> record) throws JsonProcessingException {
		UserKafka userKafka = objectMapper.readValue(record.value(), UserKafka.class);
		String userKafkaId = userKafka.getId();
		balanceService.saveIfNotExists(userKafkaId);
	}


	public BalanceResponse getBalance() {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		return BalanceMapper.map(balance);
	}

	public TransactionResponse chargeBalance(ChargeBalanceRequest chargeBalanceRequest) {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		if (balance.getAmount() < chargeBalanceRequest.getAmount()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
		}
		PaymentTransaction paymentTransaction = createTransaction(chargeBalanceRequest.getAmount(), balance, TransactionType.CHARGE);
		balance.setAmount(balance.getAmount() - chargeBalanceRequest.getAmount());
		if (chargeBalanceRequest.getType().equals(ChargeBalanceRequest.TypeEnum.POST_BOOST)) {
			//TODO kafka
		}

		balanceService.save(balance);
		return TransactionMapper.map(paymentTransaction);

	}


	public TransactionResponse refundBalance(RefundBalanceRequest refundBalanceRequest) {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		if (balance.getAmount() < refundBalanceRequest.getAmount()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
		}
		PaymentTransaction paymentTransaction = createTransaction(refundBalanceRequest.getAmount(), balance, TransactionType.REFUND);
		balance.setAmount(balance.getAmount() - refundBalanceRequest.getAmount());
		balanceService.save(balance);
		return TransactionMapper.map(paymentTransaction);

	}

	public TransactionResponse testFillBalance(TestFillBalanceRequest testFillBalanceRequest) {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		balance.setAmount(balance.getAmount() + testFillBalanceRequest.getAmount());
		PaymentTransaction paymentTransaction = createTransaction(testFillBalanceRequest.getAmount(), balance, TransactionType.FILL);
		balanceService.save(balance);
		return TransactionMapper.map(paymentTransaction);
	}

	@NotNull
	private PaymentTransaction createTransaction(Double amount, Balance balance, TransactionType type) {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		paymentTransaction.setAmount(amount);
		paymentTransaction.setBalance(balance);
		paymentTransaction.setType(type);
		paymentTransaction.setId(UUID.randomUUID().toString());
		paymentTransaction.setCreatedAt(LocalDateTime.now());
		transactionService.save(paymentTransaction);
		return paymentTransaction;
	}
}
