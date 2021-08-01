package com.yacovets.projectmanagement.service.exception;

public class TaskNotFoundServiceException extends Exception{
    public TaskNotFoundServiceException() {
        super("Task not found");
    }

    public TaskNotFoundServiceException(String message) {
        super(message);
    }
}
