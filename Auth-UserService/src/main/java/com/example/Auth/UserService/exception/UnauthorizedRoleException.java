package com.example.Auth.UserService.exception;

public class UnauthorizedRoleException extends RuntimeException {
    public UnauthorizedRoleException(String message) {

        super(message);
    }
}
