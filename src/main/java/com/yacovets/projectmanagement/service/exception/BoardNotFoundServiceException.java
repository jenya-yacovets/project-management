package com.yacovets.projectmanagement.service.exception;

public class BoardNotFoundServiceException extends Exception{
    public BoardNotFoundServiceException() {
        super("Board not found");
    }

    public BoardNotFoundServiceException(String message) {
        super(message);
    }
}
