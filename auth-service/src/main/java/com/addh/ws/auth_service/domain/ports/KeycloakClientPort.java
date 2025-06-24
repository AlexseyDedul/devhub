package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.api.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface KeycloakClientPort {
    void createUser(RegisterRequest request);
    AuthResponse getToken(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(HttpServletRequest request);
    UserProfileDto getUserInfo(HttpServletRequest request);
}
