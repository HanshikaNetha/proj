package com.example.Auth.UserService.exception;

public class InvalidCredintialsException extends RuntimeException {
    public InvalidCredintialsException(String message) {
        super(message);
    }
}
