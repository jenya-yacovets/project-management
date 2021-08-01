package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.HomePageDataModel;
import com.yacovets.projectmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ModelAndView indexPage() {
        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView homeView(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        HomePageDataModel projectsAndBoardsAndTasks = userService.getProjectsAndBoardsAndTasks(user);
        return new ModelAndView("home", "projectsAndBoardsAndTasks", projectsAndBoardsAndTasks);
    }
}
