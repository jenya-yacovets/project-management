package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ModelAndView userProfile(@CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        User findUser = userService.getUser(user.getId()).orElseThrow(DataNotFoundControllerException::new);
        return new ModelAndView("userProfile", "user", findUser);
    }
}
