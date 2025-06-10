package com.addh.ws.auth_service.api.controller;

import com.addh.ws.auth_service.api.application.service.AuthService;
import com.addh.ws.auth_service.api.dto.AuthResponse;
import com.addh.ws.auth_service.api.dto.LoginRequest;
import com.addh.ws.auth_service.api.dto.RegisterRequest;
import com.addh.ws.auth_service.api.dto.UserProfileDto;
import com.addh.ws.auth_service.domain.model.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("is-token-valid")
    public ResponseEntity<Boolean> isTokenValid(HttpServletRequest request) {
        return ResponseEntity.ok(authService.isTokenValid(request));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserProfileDto.fromUser(user));
    }

}
