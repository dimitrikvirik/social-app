package git.dimitrikvirik.feedapi.config;

import git.dimitrikvirik.feedapi.model.kafka.FeedNotification;
import git.dimitrikvirik.feedapi.model.kafka.UserDTO;
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
	public ReceiverOptions<String, UserDTO> userKafkaOptions(KafkaProperties kafkaProperties) {
		ReceiverOptions<String, UserDTO> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
		return basicReceiverOptions.subscription(Collections.singletonList("user"));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, UserDTO> userKafkaConsumer(ReceiverOptions<String, UserDTO> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
	}

	@Bean
	public ReactiveKafkaProducerTemplate<String, FeedNotification> notificationKafkaProducer(
			KafkaProperties properties) {
		Map<String, Object> props = properties.buildProducerProperties();
		return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
	}


}
