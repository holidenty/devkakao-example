package com.suhyeon.blogsearch.core.exception;

public class BlogSearchException extends AbstractCustomException {

    public BlogSearchException(BlogSearchExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.setStatus(exceptionCode.getHttpStatus());
        this.setMessage(exceptionCode.getMessage());
    }
}
