package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.entity.Task;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.service.TaskService;
import com.yacovets.projectmanagement.service.exception.TaskNotFoundServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteTask(@PathVariable long id, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        try {
            Task task = taskService.deleteTask(id, user);
            return new ModelAndView(String.format("redirect:/board/%d", task.getBoard().getId()), "message", "The task was successfully deleted");
        } catch (TaskNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Task not found");
        }
    }

    @PostMapping("/{id}/mark-completed")
    public ModelAndView markCompletedTask(@PathVariable long id, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        try {
            Task task = taskService.markCompletedTask(id, user);
            return new ModelAndView(String.format("redirect:/board/%d", task.getBoard().getId()), "message", "The task is marked as completed");
        } catch (TaskNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Task not found");
        }
    }
}
