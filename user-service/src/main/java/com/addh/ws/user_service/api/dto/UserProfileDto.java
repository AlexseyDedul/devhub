package com.addh.ws.user_service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private UUID userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String location;
    private Instant createdAt;
    private Instant updatedAt;
}
