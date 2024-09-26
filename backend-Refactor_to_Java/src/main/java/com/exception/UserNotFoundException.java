package com.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String account) {
        this(String.format("User %s not found.", account), null);
    }

    public UserNotFoundException(String account, Throwable cause) {
        super(account, cause);
    }
}
