package com.suhyeon.blogsearch.web.config;

import java.util.Arrays;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.suhyeon.blogsearch.web.config.GlobalExceptionHandler.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class GlobalExceptionLog {
    @AfterReturning(pointcut = "execution(* com.suhyeon.blogsearch.web.config.GlobalExceptionHandler.*(..))", returning = "result")
    public void log(JoinPoint joinPoint, Object result) {

        ErrorResponse errorResponse;

        if(result instanceof ResponseEntity responseEntity) {
            errorResponse = ErrorResponse.class.cast(responseEntity.getBody());
        } else if(result instanceof ErrorResponse er) {
            errorResponse = er;
        } else {
            log.error("ApiExceptionLog - result casting error");
            return;
        }

        if(errorResponse.getHttpStatus().is5xxServerError()) {
            Object[] arguments = joinPoint.getArgs();

            Arrays.stream(arguments)
                  .filter(Objects::nonNull)
                  .filter(this::isExceptionClass)
                  .findFirst()
                  .ifPresent(this::printStackTrace);
        }
    }

    private boolean isExceptionClass(Object argument) {
        Class<?> clazz = argument.getClass();
        while (clazz != null) {
            clazz = clazz.getSuperclass();
            if(clazz.getSuperclass().isAssignableFrom(RuntimeException.class)) {
                return true;
            }
        }
        return false;
    }

    private void printStackTrace(Object argument) {
        Arrays.stream(argument.getClass().getMethods())
              .filter(method -> "printStackTrace".equals(method.getName()))
              .findFirst()
              .ifPresent(method -> {
                  try{
                      log.error(argument.getClass().getName(), argument);
                  } catch (Exception e) {
                      log.error(e.getClass().getName(), e);
                  }
              });
    }
}
