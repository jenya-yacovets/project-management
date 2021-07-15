package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.model.UserAuthenticationModel;
import com.yacovets.projectmanagement.model.UserRegistrationModel;
import com.yacovets.projectmanagement.service.SecurityService;
import com.yacovets.projectmanagement.service.exception.EmailIsBusyServiceException;
import com.yacovets.projectmanagement.service.exception.UsernameIsBusyServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
        if(result.hasErrors()) {
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

    @GetMapping("/password-recovery")
    public ModelAndView passwordRecoveryView() {
        return new ModelAndView("authentication", "userModel", new UserAuthenticationModel());
    }
}
