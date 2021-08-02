package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.*;
import com.yacovets.projectmanagement.model.HomePageDataModel;
import com.yacovets.projectmanagement.repository.BoardRepository;
import com.yacovets.projectmanagement.repository.ProjectRepository;
import com.yacovets.projectmanagement.repository.TaskRepository;
import com.yacovets.projectmanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository, ProjectRepository projectRepository, BoardRepository boardRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
    }

    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    public HomePageDataModel getProjectsAndBoardsAndTasks(User user) {
        HomePageDataModel model = new HomePageDataModel();

        List<Project> projectList = projectRepository.findAllByTeams(user);
        model.setProjects(projectList);

        List<Board> boardList = boardRepository.findAllByProjectTeams(user);
        model.setBoards(boardList);

        List<Task> taskList = taskRepository.findAllByBoardProjectTeams(user);
        model.setTasks(taskList);

        int taskListSize = taskList.size();

        if (taskListSize > 0) {
            int countTaskCompleted = (int) taskList.stream().filter(item -> item.getCompletionAt() != null).count();
            model.setIndexCompletedStatusTasks(countTaskCompleted * 100 / taskListSize);
            model.setIndexInProcessingStatusTasks(100 - model.getIndexCompletedStatusTasks());

            int countLittlePriorityTasks = (int) taskList.stream().filter(item -> item.getPriority().equals(TaskPriorityEnum.LITTLE)).count();
            model.setIndexLittlePriorityTasks(countLittlePriorityTasks * 100 / taskListSize);

            int countMediumPriorityTasks = (int) taskList.stream().filter(item -> item.getPriority().equals(TaskPriorityEnum.MEDIUM)).count();
            model.setIndexMediumPriorityTasks(countMediumPriorityTasks * 100 / taskListSize);

            int countLargePriorityTasks = (int) taskList.stream().filter(item -> item.getPriority().equals(TaskPriorityEnum.LARGE)).count();
            model.setIndexLargePriorityTasks(countLargePriorityTasks * 100 / taskListSize);
        }

        return model;
    }
}
