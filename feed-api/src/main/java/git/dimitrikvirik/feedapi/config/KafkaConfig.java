package git.dimitrikvirik.feedapi.config;

import git.dimitrikvirik.feedapi.model.kafka.NotificationKafka;
import git.dimitrikvirik.feedapi.model.kafka.PaymentKafka;
import git.dimitrikvirik.feedapi.model.kafka.UserKafka;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Bean
	public ReceiverOptions<String, UserKafka> userKafkaOptions(KafkaProperties kafkaProperties) {
		kafkaProperties.getProperties().put("spring.json.value.default.type", "git.dimitrikvirik.feedapi.model.kafka.UserKafka");
		ReceiverOptions<String, UserKafka> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
		return basicReceiverOptions.subscription(Collections.singletonList("user"));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, UserKafka> userKafkaConsumer(ReceiverOptions<String, UserKafka> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
	}

	@Bean
	public ReactiveKafkaProducerTemplate<String, NotificationKafka> notificationKafkaProducer(
			KafkaProperties properties) {
		Map<String, Object> props = properties.buildProducerProperties();
		return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
	}

	@Bean
	public ReactiveKafkaProducerTemplate<String, PaymentKafka> paymentKafkaProducer(
			KafkaProperties properties) {
		Map<String, Object> props = properties.buildProducerProperties();
		return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
	}

	@Bean
	public ReceiverOptions<String, PaymentKafka> paymentKafkaOptions(KafkaProperties kafkaProperties) {
		kafkaProperties.getProperties().put("spring.json.value.default.type", "git.dimitrikvirik.feedapi.model.kafka.PaymentKafka");
		ReceiverOptions<String, PaymentKafka> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
		return basicReceiverOptions.subscription(Collections.singletonList("payment"));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, PaymentKafka> paymentKafkaConsumer(ReceiverOptions<String, PaymentKafka> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
	}


}
