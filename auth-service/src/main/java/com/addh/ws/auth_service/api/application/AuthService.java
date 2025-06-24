package com.addh.ws.auth_service.api.application;

import com.addh.ws.auth_service.api.dto.*;
import com.addh.ws.auth_service.domain.ports.AuthServicePort;
import com.addh.ws.auth_service.domain.ports.KeycloakClientPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServicePort {
    private final KeycloakClientPort keycloakClient;

    @Override
    public void register(RegisterRequest request) {
        keycloakClient.createUser(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return keycloakClient.getToken(request);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return keycloakClient.refreshToken(request);
    }

    @Override
    public void logout(HttpServletRequest request) {
        keycloakClient.logout(request);
    }

    @Override
    public UserProfileDto getCurrentUser(HttpServletRequest request) {
        return keycloakClient.getUserInfo(request);
    }
}
