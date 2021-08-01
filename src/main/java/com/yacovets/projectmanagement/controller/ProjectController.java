package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.ProjectCreateModel;
import com.yacovets.projectmanagement.service.ProjectService;
import com.yacovets.projectmanagement.service.exception.ProjectAliasBusyServiceException;
import com.yacovets.projectmanagement.service.exception.ProjectNotFoundServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{alias}")
    public ModelAndView getProjectView(@PathVariable String alias, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        Optional<Project> project = projectService.getProject(alias, user);
        if (project.isPresent()) {
            return new ModelAndView("project", "project", project.get());
        }
        throw new DataNotFoundControllerException("Project not found");
    }

    @GetMapping("/list")
    public ModelAndView listProjectView(@CurrentSecurityContext(expression = "authentication") Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Project> allProject = projectService.getAllProject(user);
        return new ModelAndView("projectList", "projectList", allProject);
    }

    @GetMapping("/create")
    public ModelAndView createProjectView(){
        return new ModelAndView("projectCreate", "project", new ProjectCreateModel());
    }

    @PostMapping("/create")
    public ModelAndView createProjectHandler(@Valid @ModelAttribute("project") ProjectCreateModel project, BindingResult result, @CurrentSecurityContext(expression = "authentication") Authentication authentication){
        if (result.hasErrors()) {
            return new ModelAndView("projectCreate", result.getModel());
        }

        User user = (User) authentication.getPrincipal();

        try {
            Project newProject = projectService.createProject(project, user);
            return new ModelAndView(String.format("redirect:/project/%s", newProject.getAlias()));
        } catch (ProjectAliasBusyServiceException e) {
            result.addError(new FieldError("project", "alias", "Project alias is busy"));
            return new ModelAndView("projectCreate", result.getModel());
        }
    }

    @PostMapping("/{alias}/delete")
    public ModelAndView deleteProject(@PathVariable String alias, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        try {
            projectService.deleteProject(alias, user);
            return new ModelAndView("redirect:/project/list", "message", "The project was successfully deleted");
        } catch (ProjectNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Project not found");
        }
    }
}
