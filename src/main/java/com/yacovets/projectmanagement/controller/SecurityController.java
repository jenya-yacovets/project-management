package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.model.UserAuthenticationModel;
import com.yacovets.projectmanagement.model.UserPasswordRecoveryModel;
import com.yacovets.projectmanagement.model.UserRegistrationModel;
import com.yacovets.projectmanagement.model.UserSetPasswordModel;
import com.yacovets.projectmanagement.service.SecurityService;
import com.yacovets.projectmanagement.service.exception.EmailIsBusyServiceException;
import com.yacovets.projectmanagement.service.exception.EmailTokenNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UsernameIsBusyServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/security")
public class SecurityController {
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/registration")
    public ModelAndView registrationView() {
        return new ModelAndView("registration", "userModel",  new UserRegistrationModel());
    }

    @PostMapping("registration")
    public ModelAndView registrationHandler(@Valid @ModelAttribute("userModel") UserRegistrationModel user, BindingResult result) {
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            result.addError(new FieldError("userModel", "password", "Passwords don't match"));
            result.addError(new FieldError("userModel", "passwordConfirmation", "Passwords don't match"));
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", result.getModel());
        }

        try {
            securityService.registration(user);
            return new ModelAndView("redirect:/security/authentication");
        } catch (UsernameIsBusyServiceException e) {
            ModelAndView model = new ModelAndView("registration", result.getModel());
            model.addObject("message", "Username is busy");
            return model;
        } catch (EmailIsBusyServiceException e) {
            ModelAndView model = new ModelAndView("registration", result.getModel());
            model.addObject("message", "Email is busy");
            return model;
        }
    }

    @GetMapping("/authentication")
    public ModelAndView authenticationView() {
        return new ModelAndView("authentication", "userModel", new UserAuthenticationModel());
    }

    @GetMapping("/email-verification/{token}")
    public ModelAndView emailVerification(@PathVariable UUID token) {
        String message;
        HttpStatus status;
        try {
            securityService.emailVerification(token);
            message = "Email has been successfully verified";
            status = HttpStatus.OK;
        } catch (EmailTokenNotFoundServiceException e) {
            message = "The email could not be verified. Invalid link or the verification time has expired. Send a new email from your personal account";
            status = HttpStatus.BAD_REQUEST;
        }

        ModelAndView model = new ModelAndView("infoPage", "text", message);
        model.addObject("title", "Email verification");
        model.setStatus(status);
        return model;
    }

    @GetMapping("/password-recovery")
    public ModelAndView passwordRecoveryView() {
        return new ModelAndView("passwordRecovery", "userModel", new UserPasswordRecoveryModel());
    }

    @PostMapping("/password-recovery")
    public ModelAndView passwordRecoveryHandler(@Valid @ModelAttribute("userModel") UserPasswordRecoveryModel model, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("passwordRecovery", result.getModel());
        }

        try {
            securityService.passwordRecovery(model);
            return new ModelAndView("passwordRecovery", "message", "A link to restore your password has been sent to your email");
        } catch (UserNotFoundServiceException e) {
            result.addError(new FieldError("userModel", "email", "Username or Email is correct"));
            return new ModelAndView("passwordRecovery", result.getModel());
        }
    }

    @GetMapping("/password-recovery/{token}")
    public ModelAndView setPasswordRecoveryView(@PathVariable UUID token) throws DataNotFoundControllerException {
        try {
            securityService.passwordRecoveryIsActive(token);
            return new ModelAndView("setPasswordRecovery", "userModel", new UserSetPasswordModel());
        } catch (EmailTokenNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Email token password recovery not found");
        }
    }

    @PostMapping("/password-recovery/{token}")
    public ModelAndView setPasswordRecoveryHandler(@PathVariable UUID token, @Valid @ModelAttribute("userModel") UserSetPasswordModel model, BindingResult result) throws DataNotFoundControllerException {
        if (!model.getPassword().equals(model.getPasswordConfirmation())) {
            result.addError(new FieldError("userModel", "password", "Passwords don't match"));
            result.addError(new FieldError("userModel", "passwordConfirmation", "Passwords don't match"));
        }
        if (result.hasErrors()) {
            return new ModelAndView("setPasswordRecovery", result.getModel());
        }

        try {
            securityService.setPassword(model, token);
            return new ModelAndView("redirect:/security/authentication");
        } catch (EmailTokenNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Email token password recovery not found");
        }
    }
}
