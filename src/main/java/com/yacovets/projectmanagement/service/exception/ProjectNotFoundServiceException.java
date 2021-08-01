package com.yacovets.projectmanagement.service.exception;

public class ProjectNotFoundServiceException extends Exception{
    public ProjectNotFoundServiceException() {
        super("Project not found");
    }

    public ProjectNotFoundServiceException(String message) {
        super(message);
    }
}
