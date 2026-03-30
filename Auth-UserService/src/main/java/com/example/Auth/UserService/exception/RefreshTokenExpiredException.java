package com.example.Auth.UserService.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {

        super(message);
    }
}
