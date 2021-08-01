package com.yacovets.projectmanagement.controller.rest;

import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.TaskCreateModel;
import com.yacovets.projectmanagement.service.TaskService;
import com.yacovets.projectmanagement.service.exception.UserNotAccessServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/task")
public class TaskRestController {
    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create/{boardId}")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateModel model, BindingResult result, @Min(1) @PathVariable long boardId, @CurrentSecurityContext(expression = "authentication")Authentication authentication) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("The data was entered incorrectly", HttpStatus.BAD_REQUEST);
        }

        User user = (User) authentication.getPrincipal();
        try {
            taskService.createTask(model, boardId, user);
        } catch (UserNotFoundServiceException e) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        } catch (UserNotAccessServiceException e) {
            return new ResponseEntity<>("User not access", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
