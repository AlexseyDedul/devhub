package com.addh.ws.user_service.api.controller;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import com.addh.ws.user_service.infrastructure.persistense.jpa.JpaUserProfileRepository;
import com.addh.ws.user_service.infrastructure.security.CustomUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserProfileRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileEntity user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = UserProfileEntity.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email("testuser@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(List.of("USER"))
                .build();

        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void shouldGetUserProfileById() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void forbiddenGetAllUsers() throws Exception {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                Map.of(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList()
        );

        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/v1/users")
                        .param("username", "admin")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void shouldDeleteUserById() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", user.getUserId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void forbiddenForDeleteUserById() throws Exception {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                Map.of(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList()
        );

        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(delete("/api/v1/users/{id}", user.getUserId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetOwnProfile() throws Exception {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                Map.of(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList()
        );

        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateOwnProfile() throws Exception {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                Map.of(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList()
        );

        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserProfileRequest updateRequest = UpdateUserProfileRequest.builder()
                .bio("Updated bio")
                .location("New York")
                .build();

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio", is("Updated bio")))
                .andExpect(jsonPath("$.location", is("New York")));
    }

    @Test
    void shouldDeleteOwnProfile() throws Exception {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                Map.of(),
                user.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList()
        );

        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);


        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isNoContent());
    }
}