package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.controller.exception.UserNotAccessControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({NoHandlerFoundException.class, DataNotFoundControllerException.class})
    public ModelAndView notFountExceptionHandler(Exception e) {
        log.warn("Not found handler or data:", e);
        return new ModelAndView("errors/notFound", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotAccessControllerException.class)
    public ModelAndView notAccessExceptionHandler(UserNotAccessControllerException e) {
        log.warn("User not access:", e);
        return new ModelAndView("errors/notAccess", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView otherExceptionHandler(Exception e) {
        log.error("Server error:", e);
        return new ModelAndView("errors/serverError", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
