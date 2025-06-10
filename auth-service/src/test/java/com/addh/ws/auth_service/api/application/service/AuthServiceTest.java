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
import com.addh.ws.auth_service.domain.ports.TokenServicePort;
import com.addh.ws.auth_service.domain.ports.UserRepositoryPort;
import com.addh.ws.auth_service.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenServicePort tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        Role role = Role.builder()
                .roleType(RoleType.USER)
                .build();
        RegisterRequest request = new RegisterRequest("test@example.com", "password", "John", "Doe");

        String encodedPassword = "encodedPassword";
        User user = new User(null,
                "test@example.com",
                encodedPassword,
                "John",
                "Doe",
                true,
                Instant.now(),
                Set.of(role)
        );

        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // when
        AuthResponse response = authService.register(request);

        // then
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());

        verify(userRepository).save(any(User.class));
        verify(tokenService).saveUserToken(eq(user), eq("access-token"), eq(TokenType.ACCESS_TOKEN));
        verify(tokenService).saveUserToken(eq(user), eq("refresh-token"), eq(TokenType.REFRESH_TOKEN));
    }

    @Test
    void shouldThrowExceptionWhenDefaultRoleNotFound() {
        RegisterRequest request = new RegisterRequest("test@example.com", "pass", "John", "Doe");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Default user role not found");
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest("test@example.com", "pass");
        User user = User.builder().email("test@example.com").build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("token");
        assertThat(response.refreshToken()).isEqualTo("refresh");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        LoginRequest request = new LoginRequest("test@example.com", "pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User with email");
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        User user = User.builder().email("test@example.com").build();
        Token token = Token.builder()
                .token("valid-refresh")
                .tokenType(TokenType.REFRESH_TOKEN)
                .build();

        when(req.getHeader("Authorization")).thenReturn("Bearer valid-refresh");
        when(tokenService.findByToken("valid-refresh")).thenReturn(Optional.of(token));
        when(jwtService.extractUsername("valid-refresh")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-refresh", user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-ref-access");


        AuthResponse response = authService.refreshToken(req);
        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-ref-access");
    }

    @Test
    void shouldThrowExceptionWhenNoAuthorizationHeader() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn(null);

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Missing refresh token");
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer valid");
        when(tokenService.isTokenValid("valid")).thenReturn(true);

        Boolean valid = authService.isTokenValid(req);
        assertThat(valid).isTrue();
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        User user = User.builder().email("test@example.com").build();

        when(req.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractUsername("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        authService.logout(req);
        verify(tokenService).revokeAllUserTokens(user);
    }
}