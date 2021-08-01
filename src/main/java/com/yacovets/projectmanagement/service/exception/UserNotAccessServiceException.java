package com.yacovets.projectmanagement.service.exception;

public class UserNotAccessServiceException extends Exception{
    public UserNotAccessServiceException() {
        super("User not access");
    }

    public UserNotAccessServiceException(String message) {
        super(message);
    }
}
