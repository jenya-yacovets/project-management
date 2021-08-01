package com.yacovets.projectmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_tokens")
@EntityListeners(AuditingEntityListener.class)
public class EmailToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne()
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private EmailTokenTypeEnum type;

    @Column(columnDefinition = "boolean default false")
    private boolean isUsed;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public EmailToken(User user, EmailTokenTypeEnum type, LocalDateTime expiredAt) {
        this.user = user;
        this.type = type;
        this.expiredAt = expiredAt;
    }
}
