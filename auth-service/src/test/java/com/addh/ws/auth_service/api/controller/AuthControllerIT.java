package com.addh.ws.auth_service.api.controller;

import com.addh.ws.auth_service.api.dto.LoginRequest;
import com.addh.ws.auth_service.api.dto.RegisterRequest;
import com.addh.ws.auth_service.domain.model.User;
import com.addh.ws.auth_service.domain.ports.UserRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    private String acToken;
    private String refToken;

    @BeforeEach
    void setUp() throws Exception {
        String email = "testuser_" + UUID.randomUUID() + "@email.com";
        String password = "password";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName("main")
                .lastName("main")
                .enabled(true)
                .build();

        User currentUser = userRepositoryPort.save(user);;

        LoginRequest request = new LoginRequest(currentUser.getEmail(), password);

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode responseJson = objectMapper.readTree(response);
        acToken = responseJson.get("accessToken").asText();
        refToken = responseJson.get("refreshToken").asText();
    }

    @Test
    void register_shouldCreateUserAndReturnTokens() throws Exception {
        RegisterRequest request = new RegisterRequest("john@example.com", "password", "John", "Doe");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_shouldAuthenticateUserAndReturnTokens() throws Exception {
        String password = "password";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("email@email.com")
                .password(passwordEncoder.encode(password))
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .build();

        userRepositoryPort.save(user);

        LoginRequest request = new LoginRequest(user.getEmail(), password);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken() throws Exception {
        // This test assumes token is obtained from /login
        String refreshToken = "Bearer " + refToken;

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .header("Authorization", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void me_shouldReturnCurrentUser() throws Exception {
        String accessToken = "Bearer " + acToken;

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void logout_shouldRevokeTokens() throws Exception {
        String accessToken = "Bearer " + acToken;

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", accessToken))
                .andExpect(status().isNoContent());
    }
}