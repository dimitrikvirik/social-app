package git.dimitrikvirik.feedapi.exception;

import org.springframework.http.HttpStatus;

record ExceptionRule(Class<?> exceptionClass, HttpStatus status){}
