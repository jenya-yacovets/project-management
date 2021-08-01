package com.yacovets.projectmanagement.repository;

import com.yacovets.projectmanagement.entity.Task;
import com.yacovets.projectmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByIdAndBoardProjectTeams(long id, User user);
    Optional<Task> findByIdAndBoardProjectTeams(long id, User user);
    List<Task> findAllByBoardProjectTeams(User user);
}
