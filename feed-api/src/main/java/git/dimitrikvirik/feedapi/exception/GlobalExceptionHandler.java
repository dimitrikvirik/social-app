package git.dimitrikvirik.feedapi.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.generated.feedapi.model.ErrorResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

	@Value("${spring.application.name}")
	private String applicationName;

	@SneakyThrows
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		if (ex instanceof ResponseStatusException) {
			System.out.println("GlobalExceptionHandler.handle 1");
			ErrorResponse errorResponse = ErrorResponse.builder()
					.application(applicationName)
					.message(ex.getMessage())
					.path(exchange.getRequest().getPath().toString())
					.exceptionName(ex.getClass().getName())
					.build();
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
			exchange.getResponse().setStatusCode(((ResponseStatusException) ex).getStatusCode());
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

			return exchange.getResponse().writeWith(Mono.just(buffer));
		}else{

			ErrorResponse errorResponse = ErrorResponse.builder()
					.application(applicationName)
					.message(ex.getMessage())
					.path(exchange.getRequest().getPath().toString())
					.exceptionName(ex.getClass().getName())
					.build();
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
			exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

			return exchange.getResponse().writeWith(Mono.just(buffer));
		}
	}
}
