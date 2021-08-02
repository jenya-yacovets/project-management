package com.yacovets.projectmanagement.repository;

import com.yacovets.projectmanagement.entity.EmailToken;
import com.yacovets.projectmanagement.entity.EmailTokenTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailTokenRepository extends JpaRepository<EmailToken, UUID> {
    Optional<EmailToken> findByIdAndAndIsUsedAndExpiredAtGreaterThanEqualAndType(UUID id, boolean used, LocalDateTime createdAt, EmailTokenTypeEnum type);
    boolean existsByIdAndAndIsUsedAndExpiredAtGreaterThanEqualAndType(UUID id, boolean used, LocalDateTime createdAt, EmailTokenTypeEnum type);
    Optional<EmailToken> findByUserUsernameAndType(String username, EmailTokenTypeEnum type);
}
