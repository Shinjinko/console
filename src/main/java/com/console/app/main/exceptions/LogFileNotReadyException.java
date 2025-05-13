package com.console.app.main.exceptions;

public class LogFileNotReadyException extends RuntimeException {
    public LogFileNotReadyException(String message) {
        super(message);
    }
}
