package com.addh.ws.auth_service.api.dto;

public record RegisterRequest (
        String email,
        String password,
        String username,
        String firstName,
        String lastName) {
}
