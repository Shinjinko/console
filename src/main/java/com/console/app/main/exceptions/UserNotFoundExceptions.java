package com.console.app.main.exceptions;

public class UserNotFoundExceptions extends RuntimeException {
    public UserNotFoundExceptions(String message) {
        super(message);
    }
}