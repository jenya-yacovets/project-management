package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.Task;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.HomePageDataModel;
import com.yacovets.projectmanagement.repository.BoardRepository;
import com.yacovets.projectmanagement.repository.ProjectRepository;
import com.yacovets.projectmanagement.repository.TaskRepository;
import com.yacovets.projectmanagement.repository.UserRepository;
import com.yacovets.projectmanagement.util.CollectionUtil;
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
    private final CollectionUtil collectionUtil;

    public UserService(UserRepository userRepository, ProjectRepository projectRepository, BoardRepository boardRepository, TaskRepository taskRepository, CollectionUtil collectionUtil) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
        this.collectionUtil = collectionUtil;
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
        model.setCountTaskCompleted(taskList.stream().filter(item -> item.getCompletionAt() != null).count());

        return model;
    }
}
