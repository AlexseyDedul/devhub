package com.addh.ws.auth_service.api.application.service;

import com.addh.ws.auth_service.api.dto.AuthResponse;
import com.addh.ws.auth_service.api.dto.LoginRequest;
import com.addh.ws.auth_service.api.dto.RegisterRequest;
import com.addh.ws.auth_service.domain.enums.RoleType;
import com.addh.ws.auth_service.domain.enums.TokenType;
import com.addh.ws.auth_service.domain.model.Role;
import com.addh.ws.auth_service.domain.model.Token;
import com.addh.ws.auth_service.domain.model.User;
import com.addh.ws.auth_service.domain.ports.RoleRepositoryPort;
import com.addh.ws.auth_service.domain.ports.TokenRepositoryPort;
import com.addh.ws.auth_service.domain.ports.TokenServicePort;
import com.addh.ws.auth_service.domain.ports.UserRepositoryPort;
import com.addh.ws.auth_service.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenServicePort tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        Role role = roleRepository.findByName(RoleType.USER).orElseThrow(
                () -> {
                    log.error("Default user role not found");
                    return new RuntimeException("Default user role not found");
                }
        );

        User user = User.builder()
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .enabled(true)
                .createdAt(Instant.now())
                .roles(Set.of(role))
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        tokenService.saveUserToken(savedUser, token, TokenType.ACCESS_TOKEN);
        tokenService.saveUserToken(savedUser, refreshToken, TokenType.REFRESH_TOKEN);

        return new AuthResponse(token, refreshToken);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Login request: {}", loginRequest);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                () -> {
                    log.error("User with email {} not found", loginRequest.email());
                    return new RuntimeException("User with email %s not found".formatted(loginRequest.email()));
                }
        );
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenService.saveUserToken(user, token, TokenType.ACCESS_TOKEN);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH_TOKEN);

        return new AuthResponse(token, refreshToken);
    }

    public AuthResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing refresh token");
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);

        Optional<Token> storedToken = tokenService.findByToken(refreshToken);
        if (storedToken.isEmpty() || (storedToken.get().getTokenType() != TokenType.REFRESH_TOKEN)) {
            throw new RuntimeException("Token is not a refresh token");
        }

        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        tokenService.revokeAllUserTokens(user);

        var newAccessToken = jwtService.generateToken(user);
        tokenService.saveUserToken(user, newAccessToken, TokenType.ACCESS_TOKEN);

        var newRefreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(user, newRefreshToken, TokenType.REFRESH_TOKEN);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public Boolean isTokenValid(HttpServletRequest request){
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing refresh token");
        }

        token = authHeader.substring(7);
        return tokenService.isTokenValid(token);
    }

    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        token = authHeader.substring(7);
        final String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        tokenService.revokeAllUserTokens(user);
    }
}
