package com.suhyeon.blogsearch.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
public enum BlogSearchExceptionCode implements ExceptionCodeBase {
    CACHE_TYPE_NOT_FOUND(INTERNAL_SERVER_ERROR, "캐시타입이 존재하지 않습니다"),
    LOCK_TIMEOUT(INTERNAL_SERVER_ERROR, "락 타임아웃"),
    ;
    private HttpStatus httpStatus;
    private String message;

    BlogSearchExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }
}
