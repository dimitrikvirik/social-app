package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.kafka.NotificationKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final ReactiveKafkaProducerTemplate<String, NotificationKafka> template;
	private static final String NOTIFICATION_TOPIC = "notification";

	public Mono<SenderResult<Void>> sendNotification(NotificationKafka notification) {
		 return template.send(NOTIFICATION_TOPIC, notification.getSenderUserId(), notification);
	}

}
