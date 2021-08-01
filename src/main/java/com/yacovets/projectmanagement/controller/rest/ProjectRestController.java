package com.yacovets.projectmanagement.controller.rest;

import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.ProjectAddToTeamModel;
import com.yacovets.projectmanagement.service.ProjectService;
import com.yacovets.projectmanagement.service.exception.ProjectNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/project")
public class ProjectRestController {
    private final ProjectService projectService;

    public ProjectRestController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/add-to-team/{alias}")
    public ResponseEntity<?> addToTeam(@Valid @RequestBody ProjectAddToTeamModel model, BindingResult result, @PathVariable String alias, @CurrentSecurityContext(expression = "authentication")Authentication authentication) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("The data was entered incorrectly", HttpStatus.BAD_REQUEST);
        }

        User user = (User) authentication.getPrincipal();
        try {
            projectService.addUserToTeam(model, alias, user);
        } catch (UserNotFoundServiceException e) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        } catch (ProjectNotFoundServiceException e) {
            return new ResponseEntity<>("Project not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
