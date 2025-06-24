package com.addh.ws.auth_service.api.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn) {
}
