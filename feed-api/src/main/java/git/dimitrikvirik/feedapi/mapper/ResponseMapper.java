package git.dimitrikvirik.feedapi.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseMapper {

	private ResponseMapper() {}

	public static <T> Mono<ResponseEntity<T>> toResponseEntity(HttpStatus status, Mono<T> body) {
		return body.map(b -> ResponseEntity.status(status).body(b));
	}

	public static <T> Mono<ResponseEntity<Flux<T>>> toResponseEntity(HttpStatus status, Flux<T> body) {
		return Mono.just(ResponseEntity.status(status).body(body));
	}

	public static <T> Mono<ResponseEntity<T>> toResponseEntity(HttpStatus status) {
		return Mono.just(ResponseEntity.status(status).build());
	}

}
