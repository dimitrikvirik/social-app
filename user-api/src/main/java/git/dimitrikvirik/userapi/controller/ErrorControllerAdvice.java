package git.dimitrikvirik.userapi.controller;

import git.dimitrikvirik.userapi.mapper.ErrorMapper;
import git.dimitrikvirik.userapi.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorControllerAdvice {
	@Value("${spring.application.name}")
	private String applicationName;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validExceptionHandler(MethodArgumentNotValidException ex,
															   HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String MethodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,
																				HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String MethodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> responseStatusExceptionHandler(ResponseStatusException exception,
																		HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = exception.getClass().getName();
		String methodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(exception, applicationName,
				methodName, exceptionName, request.getRequestURI()), exception.getStatusCode());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> responseStatusExceptionHandler(AccessDeniedException exception,
																		HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = exception.getClass().getName();
		String methodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(exception, applicationName,
				methodName, exceptionName, request.getRequestURI()), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex,
																HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String MethodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
				HttpStatus.PAYLOAD_TOO_LARGE);
	}


	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> httpMessageNotReadableExceptionHandler(MissingServletRequestPartException ex,
																				HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String MethodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> missingServletRequestParameterException(MissingServletRequestParameterException ex, HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String methodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, methodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String methodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, methodName, exceptionName, request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> otherExceptionHandler(Exception ex,
															   HandlerMethod handlerMethod, HttpServletRequest request) {
		String exceptionName = ex.getClass().getName();
		String MethodName = handlerMethod.getMethod().getName();
		return new ResponseEntity<>(ErrorMapper.of(ex, applicationName, MethodName, exceptionName, request.getRequestURI()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
