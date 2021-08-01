package com.yacovets.projectmanagement.controller.exception;

public class UserNotAccessControllerException extends Exception{
    public UserNotAccessControllerException() {
        super("User not access");
    }

    public UserNotAccessControllerException(String message) {
        super(message);
    }
}
