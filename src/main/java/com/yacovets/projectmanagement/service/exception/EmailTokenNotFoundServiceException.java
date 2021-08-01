package com.yacovets.projectmanagement.service.exception;

public class EmailTokenNotFoundServiceException extends Exception {
    public EmailTokenNotFoundServiceException() {
        super("Email token not found");
    }

    public EmailTokenNotFoundServiceException(String message) {
        super(message);
    }
}
