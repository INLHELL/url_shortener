package com.example.urlshortener.advice;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

/**
 * Global application level exception handler.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(final Exception exception) {
        System.out.println(exception.getClass());
        System.out.println(exception.getMessage());
        return new ResponseEntity<>(ErrorResponse
                .builder()
                .errorCode(INTERNAL_SERVER_ERROR.value())
                .message("Please contact your administrator")
                .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException exception) {
        return new ResponseEntity<>(ErrorResponse
                .builder()
                .errorCode(NOT_IMPLEMENTED.value())
                .message("The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.")
                .build(), NOT_IMPLEMENTED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException exception) {
        return new ResponseEntity<>(ErrorResponse
                .builder()
                .errorCode(UNSUPPORTED_MEDIA_TYPE.value())
                .message("The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.")
                .build(), UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(ErrorResponse
                .builder()
                .errorCode(BAD_REQUEST.value())
                .message("The request could not be understood by the server due to malformed syntax.")
                .build(), BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public Map handle(final MethodArgumentNotValidException exception) {
        return error(exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList()));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    public Map handle(final ConstraintViolationException exception) {
        return error(exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
    }

    private Map error(final Object message) {
        return Collections.singletonMap("error", message);
    }

    @Value
    @Builder
    public static final class ErrorResponse {
        private int errorCode;
        private String message;
    }
}
