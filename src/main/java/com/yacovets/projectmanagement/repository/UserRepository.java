package com.yacovets.projectmanagement.repository;

import com.yacovets.projectmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsernameAndEmail(String username, String email);
    Optional<User> findById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
