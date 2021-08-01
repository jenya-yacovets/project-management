package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.ProjectAddToTeamModel;
import com.yacovets.projectmanagement.model.ProjectCreateModel;
import com.yacovets.projectmanagement.repository.ProjectRepository;
import com.yacovets.projectmanagement.repository.UserRepository;
import com.yacovets.projectmanagement.service.exception.ProjectAliasBusyServiceException;
import com.yacovets.projectmanagement.service.exception.ProjectNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotAccessServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotFoundServiceException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class ProjectService {
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ProjectService(ModelMapper modelMapper, ProjectRepository projectRepository, UserRepository userRepository, EmailService emailService) {
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Project createProject(ProjectCreateModel projectModel, User user) throws ProjectAliasBusyServiceException {
        if (projectRepository.existsByAlias(projectModel.getAlias())) {
            throw new ProjectAliasBusyServiceException();
        }

        Set<User> projectAdmins = new HashSet<>();
        projectAdmins.add(user);

        Project projectMapped = modelMapper.map(projectModel, Project.class);
        projectMapped.setCreator(user);
        projectMapped.setTeams(projectAdmins);
        Project saveProject = projectRepository.save(projectMapped);

        log.info("Create new project: {}", saveProject);
        return saveProject;
    }

    public Optional<Project> getProject(String alias, User user) {
        return projectRepository.findByAliasAndTeams(alias, user);
    }

    public List<Project> getAllProject(User user) {
        return projectRepository.findAllByTeams(user);
    }

    public User addUserToTeam(ProjectAddToTeamModel model, String alias, User user) throws UserNotFoundServiceException, ProjectNotFoundServiceException {
        User findUser = userRepository.findByUsernameOrEmail(model.getEmailOrUsername(), model.getEmailOrUsername()).orElseThrow(UserNotFoundServiceException::new);
        if (findUser.getId() == user.getId()) {
            throw new UserNotFoundServiceException();
        }

        Project project = projectRepository.findByAliasAndTeams(alias, user).orElseThrow(ProjectNotFoundServiceException::new);
        project.getTeams().add(findUser);
        projectRepository.save(project);
        emailService.sendMailAddToTeam(findUser.getEmail(), alias, user.getUsername(), project.getName());
        log.info("Add user to team project: {}", project);
        return findUser;
    }

    public Project deleteProject(String alias, User user) throws ProjectNotFoundServiceException {
        Project project = projectRepository.findByAliasAndTeams(alias, user).orElseThrow(ProjectNotFoundServiceException::new);
        projectRepository.delete(project);
        log.info("Success delete project: {}", project);
        return project;
    }
}
