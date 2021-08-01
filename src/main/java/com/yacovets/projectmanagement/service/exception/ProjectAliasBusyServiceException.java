package com.yacovets.projectmanagement.service.exception;

public class ProjectAliasBusyServiceException extends Exception {
    public ProjectAliasBusyServiceException(String message) {
        super(message);
    }

    public ProjectAliasBusyServiceException() {
        super("Project alias is busy");
    }
}
