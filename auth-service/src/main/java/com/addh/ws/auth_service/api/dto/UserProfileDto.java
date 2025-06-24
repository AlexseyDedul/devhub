package com.addh.ws.auth_service.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileDto(
        String id,
        String email,
        String username,
        String firstName,
        String lastName,
        List<String> roles
)
{}
