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
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

	@Value("${spring.application.name}")
	private String applicationName;

	@SneakyThrows
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		if (ex instanceof ResponseStatusException) {
			HttpStatusCode statusCode = ((ResponseStatusException) ex).getStatusCode();
			ErrorResponse errorResponse = ErrorResponse.builder()
					.application(applicationName)
					.message(((ResponseStatusException) ex).getReason())
					.path(exchange.getRequest().getPath().toString())
					.exceptionName(ex.getClass().getName())
					.status(statusCode.toString())
					.build();
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

			exchange.getResponse().setStatusCode(statusCode);
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

			return exchange.getResponse().writeWith(Mono.just(buffer));
		} else {

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
