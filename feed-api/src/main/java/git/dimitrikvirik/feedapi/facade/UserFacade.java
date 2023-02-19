package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.service.UserService;
import git.dimitrikvirik.generated.feedapi.model.UserResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserFacade {


	private final UserService userService;
	private final ReactiveKafkaConsumerTemplate<String, String> kafkaTemplate;


	@PostConstruct
	public void test() {
		kafkaTemplate.receiveAutoAck().subscribe(record -> {
			System.out.println(record.value());
		});
	}


	public Mono<ResponseEntity<UserResponse>> getUserById(String id, ServerWebExchange exchange) {
		return userService.userById(id)
				.map(user -> ResponseEntity.ok(new UserResponse()
						.id(user.getId())
						.firstname(user.getFirstname())
						.lastname(user.getLastname())
						.photo(user.getPhoto())
				)).log().defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
