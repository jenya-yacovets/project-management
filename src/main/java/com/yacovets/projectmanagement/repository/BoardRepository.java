package com.yacovets.projectmanagement.repository;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByIdAndProjectTeams(long id, User user);
    List<Board> findAllByProjectTeams(User user);
    boolean existsByIdAndProjectTeams(long id, User user);
}
