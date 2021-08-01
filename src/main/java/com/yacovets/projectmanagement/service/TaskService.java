package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.Task;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.TaskCreateModel;
import com.yacovets.projectmanagement.repository.BoardRepository;
import com.yacovets.projectmanagement.repository.TaskRepository;
import com.yacovets.projectmanagement.repository.UserRepository;
import com.yacovets.projectmanagement.service.exception.TaskNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotAccessServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public TaskService(TaskRepository taskRepository, BoardRepository boardRepository, UserRepository userRepository, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Task createTask(TaskCreateModel model, long boardId, User user) throws UserNotFoundServiceException, UserNotAccessServiceException {
        if (!boardRepository.existsByIdAndProjectTeams(boardId, user)) {
            throw new UserNotAccessServiceException();
        }

        User performer = userRepository.findById(model.getPerformer()).orElseThrow(UserNotFoundServiceException::new);
        if (!boardRepository.existsByIdAndProjectTeams(boardId, performer)) {
            throw new UserNotAccessServiceException();
        }

        Task task = new Task();
        task.setText(model.getTask());
        task.setPerformer(performer);
        task.setCreator(user);
        task.setPriority(model.getPriority());
        task.setBoard(new Board(boardId));

        Task saveTask = taskRepository.save(task);
        emailService.sendMailAddNewTask(performer.getEmail(), boardId, user.getUsername());
        log.info("Create new task: {}", saveTask);
        return saveTask;
    }

    public Task deleteTask(long id, User user) throws TaskNotFoundServiceException {
        Task task = taskRepository.findByIdAndBoardProjectTeams(id, user).orElseThrow(TaskNotFoundServiceException::new);
        taskRepository.delete(task);
        log.info("Success delete task: {}", task);
        return task;
    }

    public Task markCompletedTask(long id, User user) throws TaskNotFoundServiceException {
        Task task = taskRepository.findByIdAndBoardProjectTeams(id, user).orElseThrow(TaskNotFoundServiceException::new);
        task.setCompletionAt(LocalDateTime.now());
        taskRepository.save(task);
        log.info("Mark completed task: {}", task);
        return task;
    }
}
