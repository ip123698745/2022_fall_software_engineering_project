package com.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(long id) {
        super(String.valueOf(id));
    }
    public ProjectNotFoundException(String account) {
        super(String.valueOf(account));
    }
}
