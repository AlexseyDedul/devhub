package com.addh.ws.user_service.infrastructure.persistense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileEntity {
    @Id
    private UUID userId;

    private String displayName;

    private String avatarUrl;

    @Column(length = 2048)
    private String bio;

    private String location;

    private Instant createdAt;

    private Instant updatedAt;
}
