package com.bobrov.checkApp.controller;

import com.bobrov.checkApp.service.exception.ExceptionResponse;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    private final DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
            
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleException(NotFoundException exception) {
        log.error(exception.getMessage(), exception);

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .type(exception.getClass().getSimpleName())
                .createdAt(LocalDateTime.now().format(dateTime))
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ExceptionResponse handleException(ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .type(exception.getClass().getSimpleName())
                .createdAt(LocalDateTime.now().format(dateTime))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .type(exception.getClass().getSimpleName())
                .createdAt(LocalDateTime.now().format(dateTime))
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }
}
