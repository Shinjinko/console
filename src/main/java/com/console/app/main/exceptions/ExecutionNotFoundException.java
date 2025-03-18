package com.console.app.main.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ExecutionNotFoundException extends RuntimeException {
    public ExecutionNotFoundException(String message) {
        super(message);
    }

    @ExceptionHandler(ExecutionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleExecutionNotFoundException(ExecutionNotFoundException ex) {
        return ex.getMessage();
    }
}
