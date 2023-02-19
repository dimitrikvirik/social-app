package git.dimitrikvirik.feedapi.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class KafkaConfig {

	@Bean
	public ReceiverOptions<String, String> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
		ReceiverOptions<String, String> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
		return basicReceiverOptions.subscription(Collections.singletonList("user"));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate(ReceiverOptions<String, String> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<String, String>(kafkaReceiverOptions);
	}


}
