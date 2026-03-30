package com.example.Auth.UserService.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {

        super(message);
    }
}
