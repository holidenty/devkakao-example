package com.suhyeon.blogsearch.web.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException exception, WebRequest request) {
        var errorMessages = exception.getBindingResult().getFieldErrors()
                                     .stream()
                                     .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                                     .toList();

        return ErrorResponse.builder()
                            .httpStatus(BAD_REQUEST)
                            .message(String.join(",", errorMessages))
                            .messages(errorMessages)
                            .path(request.getDescription(false))
                            .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        var errorMessages = exception.getBindingResult().getFieldErrors()
                                              .stream()
                                              .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                                              .toList();

        return ErrorResponse.builder()
                            .httpStatus(BAD_REQUEST)
                            .message(String.join(",", errorMessages))
                            .messages(errorMessages)
                            .path(request.getDescription(false))
                            .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {
        var errorMessages = exception.getConstraintViolations()
                                              .stream()
                                              .map(cv -> cv.getPropertyPath() + " : " + cv.getMessage())
                                              .toList();

        return ErrorResponse.builder()
                            .httpStatus(BAD_REQUEST)
                            .message(String.join(",", errorMessages))
                            .messages(errorMessages)
                            .path(request.getDescription(false))
                            .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception exception, WebRequest request) {
        return ErrorResponse.builder()
                            .httpStatus(INTERNAL_SERVER_ERROR)
                            .path(request.getDescription(false))
                            .message(exception.getMessage())
                            .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorResponse handHttpClientErrorException(
        HttpClientErrorException httpClientErrorException, WebRequest request) {

        return ErrorResponse.builder()
                            .httpStatus(INTERNAL_SERVER_ERROR)
                            .path(request.getDescription(false))
                            .messages(Arrays.asList(httpClientErrorException.getMessage(),
                                                    httpClientErrorException.getResponseBodyAsString()))
                            .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WebClientResponseException.class)
    public ErrorResponse webClientResponseException(
        WebClientResponseException webClientResponseException, WebRequest request) {

        return ErrorResponse.builder()
                            .httpStatus(HttpStatus.valueOf(webClientResponseException.getStatusCode().value()))
                            .path(request.getDescription(false))
                            .message(webClientResponseException.getMessage())
                            .messages(Arrays.asList(webClientResponseException.getMessage(),
                                                    webClientResponseException.getResponseBodyAsString()))
                            .build();
    }

    @Getter
    public static class ErrorResponse {
        private LocalDateTime date = LocalDateTime.now();
        private Integer statusCode;
        private String status;
        private String path;
        private String message;
        @JsonInclude(NON_EMPTY)
        private List<String> messages;
        @JsonIgnore
        private HttpStatus httpStatus;
        @JsonInclude(NON_NULL)
        private String errorCode;

        @Builder
        public ErrorResponse(HttpStatus httpStatus, List<String> messages, String path, String message,
            String errorCode) {
            this.statusCode = httpStatus.value();
            this.status = httpStatus.getReasonPhrase();
            this.path = path;
            this.message = message;
            this.messages = messages;
            this.httpStatus = httpStatus;
            this.errorCode = errorCode;
        }
    }
}
