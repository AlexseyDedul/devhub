package com.addh.ws.auth_service.api.dto;

public record RegisterRequest (
        String email,
        String password,
        String firstName,
        String lastName) {
}
