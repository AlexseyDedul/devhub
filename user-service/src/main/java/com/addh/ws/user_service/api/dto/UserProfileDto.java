package com.addh.ws.user_service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private List<String> roles;
    private String avatarUrl;
    private String bio;
    private String location;
}
