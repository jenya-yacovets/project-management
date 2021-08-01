package com.yacovets.projectmanagement.controller.exception;

public class DataNotFoundControllerException extends Exception{
    public DataNotFoundControllerException(String message) {
        super(message);
    }

    public DataNotFoundControllerException() {
        super("Data not found");
    }
}
