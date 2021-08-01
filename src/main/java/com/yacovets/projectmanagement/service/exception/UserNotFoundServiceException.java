package com.yacovets.projectmanagement.service.exception;

public class UserNotFoundServiceException extends Exception{
    public UserNotFoundServiceException() {
        super("User not found");
    }

    public UserNotFoundServiceException(String message) {
        super(message);
    }
}
