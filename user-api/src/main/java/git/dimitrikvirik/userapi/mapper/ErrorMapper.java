package git.dimitrikvirik.userapi.mapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import git.dimitrikvirik.userapi.model.ErrorResponse;
import lombok.Data;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ErrorMapper {

	public static ErrorResponse of(MethodArgumentNotValidException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.BAD_REQUEST.name();
		String msg = e.getFieldErrors().stream()
				.map(f -> "Wrong " + f.getField() + ". " + StringUtils.capitalize(Objects.requireNonNull(f.getDefaultMessage())))
				.collect(Collectors.toList()).toString();

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}


	public static ErrorResponse of(MissingServletRequestPartException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.BAD_REQUEST.name();
		String msg = e.getMessage();

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(HttpMessageNotReadableException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.BAD_REQUEST.name();


		InvalidFormatException invalidFormatException = (InvalidFormatException) e.getMostSpecificCause();
		Class<?> targetType = invalidFormatException.getTargetType();

		String simpleName = targetType.getSimpleName();
		String msg = invalidFormatException.getValue() + " can't be parsed to " + simpleName + ".";
		if (targetType.isEnum()) {
			String fields = Arrays.stream(targetType.getFields()).map(Field::getName).collect(Collectors.joining(", "));
			msg += simpleName + " must be one of this: " + fields;
		}

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(ResponseStatusException exception, String application_name, String method_name, String exception_name, String path) {
		var status = exception.getStatusCode().toString();
		String message = exception.getReason();
		return new ErrorResponse(message, application_name, method_name, exception_name, path, status);

	}

	public static ErrorResponse of(MaxUploadSizeExceededException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.PAYLOAD_TOO_LARGE.name();
		final String maxSize = FileUtils.byteCountToDisplaySize(e.getMaxUploadSize());
		String msg = "File size exceeds limit of " + maxSize + " !";

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(MissingServletRequestParameterException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.BAD_REQUEST.name();
		String message = e.getMessage();
		return new ErrorResponse(message, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(MethodArgumentTypeMismatchException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.BAD_REQUEST.name();
		String message = e.getMessage();
		return new ErrorResponse(message, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(AccessDeniedException e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.FORBIDDEN.name();
		String msg = e.getMessage();

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}

	public static ErrorResponse of(Exception e, String application_name, String method_name, String exception_name, String path) {
		var status = HttpStatus.INTERNAL_SERVER_ERROR.name();
		String msg = e.getMessage();

		return new ErrorResponse(msg, application_name, method_name, exception_name, path, status);
	}


}
