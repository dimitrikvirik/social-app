package git.dimitrikvirik.feedapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ErrorControllerAdvice {
	@Value("${spring.application.name}")
	private String applicationName;

//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public Mono<ResponseEntity<ErrorResponse> validExceptionHandler(MethodArgumentNotValidException ex,
//																	HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String MethodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(HttpMessageNotReadableException.class)
//	public Mono<ResponseEntity<ErrorResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,
//																				HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String MethodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
//				HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(ResponseStatusException.class)
//	public Mono<ResponseEntity<ErrorResponse> responseStatusExceptionHandler(ResponseStatusException exception,
//																		HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = exception.getClass().getName();
//		String methodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(exception, applicationName,
//				methodName, exceptionName, request.getRequestURI()), exception.getStatusCode());
//	}
//
//	@ExceptionHandler(AccessDeniedException.class)
//	public Mono<ResponseEntity<ErrorResponse> responseStatusExceptionHandler(AccessDeniedException exception,
//																		HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = exception.getClass().getName();
//		String methodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(exception, applicationName,
//				methodName, exceptionName, request.getRequestURI()), HttpStatus.FORBIDDEN);
//	}
//
//	@ExceptionHandler(MaxUploadSizeExceededException.class)
//	public Mono<ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex,
//																HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String MethodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
//				HttpStatus.PAYLOAD_TOO_LARGE);
//	}
//
//
//	@ExceptionHandler(MissingServletRequestPartException.class)
//	public Mono<ResponseEntity<ErrorResponse>> httpMessageNotReadableExceptionHandler(MissingServletRequestPartException ex,
//																				HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String MethodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
//				HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(MissingServletRequestParameterException.class)
//	public Mono<ResponseEntity<ErrorResponse>> missingServletRequestParameterException(MissingServletRequestParameterException ex, HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String methodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, methodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
//	public Mono<ResponseEntity<ErrorResponse>> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HandlerMethod handlerMethod, HttpServletRequest request) {
//		String exceptionName = ex.getClass().getName();
//		String methodName = handlerMethod.getMethod().getName();
//		return new Mono<ResponseEntity<>(ErrorMapper.of(ex, applicationName, methodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
//	}


	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<String>> otherExceptionHandler(Exception ex) {
		String exceptionName = ex.getClass().getName();
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
	}

}
