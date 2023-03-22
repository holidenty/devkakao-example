package com.suhyeon.blogsearch.core.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import lombok.Getter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
public class AbstractCustomException extends RuntimeException{
    protected String code;
    protected HttpStatus status;
    protected List<String> messages;

    public AbstractCustomException(String message) {
        super(message);
    }

    protected void setMessage(String message) {
        this.messages = Optional.ofNullable(message).map(Arrays::asList).orElse(Collections.emptyList());
    }

    protected void setStatus(HttpStatus status) {
        this.status = Optional.ofNullable(status).orElse(INTERNAL_SERVER_ERROR);
    }
}
