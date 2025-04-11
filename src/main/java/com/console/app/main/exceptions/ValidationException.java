package com.console.app.main.exceptions;

public class ValidationException extends RuntimeException {
    private final int errorCode;

    public ValidationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}