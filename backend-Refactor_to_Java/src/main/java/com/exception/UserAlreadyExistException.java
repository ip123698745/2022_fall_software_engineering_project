package com.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String account) {
        super(account);
    }
}
