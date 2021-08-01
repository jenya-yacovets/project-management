package com.yacovets.projectmanagement.repository;

import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    boolean existsByAlias(String alias);
    Optional<Project> findByAliasAndTeams(String alias, User user);
    List<Project> findAllByTeams(User user);
    boolean existsByAliasAndTeams(String alias, User user);
}
