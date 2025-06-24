package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.api.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthServicePort {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(HttpServletRequest request);
    UserProfileDto getCurrentUser(HttpServletRequest request);
}
