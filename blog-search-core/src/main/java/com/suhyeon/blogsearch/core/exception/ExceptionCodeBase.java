package com.suhyeon.blogsearch.core.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCodeBase {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}
