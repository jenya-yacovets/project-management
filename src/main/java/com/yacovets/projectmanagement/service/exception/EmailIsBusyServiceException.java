package com.yacovets.projectmanagement.service.exception;

public class EmailIsBusyServiceException extends Exception{
    public EmailIsBusyServiceException(String message) {
        super(message);
    }

    public EmailIsBusyServiceException() {
        super("Email is busy");
    }
}
