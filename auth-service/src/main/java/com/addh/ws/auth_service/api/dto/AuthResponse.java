package com.addh.ws.auth_service.api.dto;

public record AuthResponse (String accessToken, String refreshToken) {
}
