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
import git.dimitrikvirik.paymentapi.model.kafka.PaymentKafka;
import git.dimitrikvirik.paymentapi.model.kafka.PaymentStatus;
import git.dimitrikvirik.paymentapi.model.kafka.PaymentType;
import git.dimitrikvirik.paymentapi.model.kafka.UserKafka;
import git.dimitrikvirik.paymentapi.service.BalanceService;
import git.dimitrikvirik.paymentapi.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BalanceFacade {

	private final BalanceService balanceService;

	private final PaymentTransactionService transactionService;

	private final ObjectMapper objectMapper;

	private final KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(topics = "user", groupId = "payment-api")
	public void userKafkaListener(ConsumerRecord<String, String> record) throws JsonProcessingException {
		UserKafka userKafka = objectMapper.readValue(record.value(), UserKafka.class);
		String userKafkaId = userKafka.getId();
		balanceService.saveIfNotExists(userKafkaId);
	}

	@KafkaListener(topics = "payment", groupId = "payment-api")
	public void paymentKafkaListener(ConsumerRecord<String, String> record) throws JsonProcessingException {
		PaymentKafka paymentKafka = objectMapper.readValue(record.value(), PaymentKafka.class);
		if (paymentKafka.getStatus().equals(PaymentStatus.SUCCESS)) {
			PaymentTransaction transaction = transactionService.findById(paymentKafka.getTransactionId());
			transaction.setStatus(PaymentStatus.SUCCESS);
			log.info("Transaction {} was successful", transaction.getId());
			transactionService.save(transaction);
		} else if (paymentKafka.getStatus().equals(PaymentStatus.FAILED)) {
			PaymentTransaction transaction = transactionService.findById(paymentKafka.getTransactionId());
			transaction.setStatus(PaymentStatus.FAILED);
			transactionService.save(transaction);
			log.info("Transaction {} was failed", transaction.getId());
			//Return money to balance if failed
			Balance balance = transaction.getBalance();
			balance.setAmount(balance.getAmount() + transaction.getAmount());
			balanceService.save(balance);
		}
	}


	public BalanceResponse getBalance() {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		return BalanceMapper.map(balance);
	}

	public TransactionResponse chargeBalance(ChargeBalanceRequest chargeBalanceRequest) {
		Balance balance = balanceService.findByUserId(UserHelper.currentUserId());
		if (balance.getAmount() < 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money");
		}
		PaymentTransaction paymentTransaction = createTransaction(5.0, balance, TransactionType.CHARGE);
		balance.setAmount(balance.getAmount() - 5);
		if (chargeBalanceRequest.getType().equals(ChargeBalanceRequest.TypeEnum.POST_BOOST)) {
			PaymentKafka payment = new PaymentKafka();
			payment.setTransactionId(paymentTransaction.getId());
			payment.setStatus(PaymentStatus.PENDING);
			payment.setType(PaymentType.POST_BOOST);
			payment.setResourceId(chargeBalanceRequest.getTargetResourceId());
			try {

				kafkaTemplate.send("payment", objectMapper.writeValueAsString(payment));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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
