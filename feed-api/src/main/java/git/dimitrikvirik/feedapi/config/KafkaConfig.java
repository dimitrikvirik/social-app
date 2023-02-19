package git.dimitrikvirik.feedapi.config;

import git.dimitrikvirik.feedapi.model.kafka.UserDTO;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class KafkaConfig {

	@Bean
	public ReceiverOptions<String, UserDTO> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
		ReceiverOptions<String, UserDTO> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
		return basicReceiverOptions.subscription(Collections.singletonList("user"));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, UserDTO> reactiveKafkaConsumerTemplate(ReceiverOptions<String, UserDTO> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
	}


}
