package com.addh.ws.user_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles = new ArrayList<>();
    private String avatarUrl;
    private String bio;
    private String location;
    private Instant updatedAt;
}
