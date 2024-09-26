package com.exception;

public class ProjectDeleteException extends RuntimeException {
    public ProjectDeleteException(String exceptionMessage) {
        super(exceptionMessage);
    }
}