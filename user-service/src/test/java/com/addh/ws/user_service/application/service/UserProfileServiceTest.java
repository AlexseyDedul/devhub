package com.addh.ws.user_service.application.service;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.api.mapper.UserProfileMapper;
import com.addh.ws.user_service.domain.exception.UserProfileNotFoundException;
import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.domain.ports.UserProfileRepositoryPort;
import com.addh.ws.user_service.infrastructure.security.CustomUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock
    private UserProfileRepositoryPort repository;

    @Mock
    private UserProfileMapper mapper;

    @InjectMocks
    private UserProfileService service;

    private UUID userId;
    private UserProfile profile;
    private UserProfileDto dto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profile = UserProfile.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        dto = new UserProfileDto(userId, "testuser", "Test", "test@example.com", "User", null, null, null, null);
    }

    @Test
    void getUserProfileById_shouldReturnProfileDto() {
        when(repository.findById(userId)).thenReturn(Optional.of(profile));
        when(mapper.toDto(profile)).thenReturn(dto);

        var result = service.getUserProfileById(userId);

        assertEquals(dto, result);
        verify(repository).findById(userId);
    }

    @Test
    void getUserProfileById_shouldThrowException_whenNotFound() {
        when(repository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserProfileNotFoundException.class, () -> service.getUserProfileById(userId));
    }

    @Test
    void updateCurrentUserProfile_shouldUpdateAndReturnDto() {
        var request = new UpdateUserProfileRequest("avatarUrl", "NewBio", "NewLocation");
        when(repository.findById(userId)).thenReturn(Optional.of(profile));
        when(repository.save(profile)).thenReturn(profile);
        when(mapper.toDto(profile)).thenReturn(dto);

        var result = service.updateCurrentUserProfile(userId, request);

        assertEquals(dto, result);
        verify(repository).save(profile);
    }

    @Test
    void syncUser_shouldCreateNewProfileIfNotExists() {
        CustomUserPrincipal principal = new CustomUserPrincipal(userId, "username", "email", "first", "last", List.of("USER"), Map.of(), List.of());

        when(repository.findById(userId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(profile);

        service.syncUser(principal);

        verify(repository).save(any());
    }
}