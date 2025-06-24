package com.addh.ws.user_service.domain.ports;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.api.filter.UserFilterRequest;
import com.addh.ws.user_service.infrastructure.security.CustomUserPrincipal;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserProfileServicePort {
    UserProfileDto getUserProfileById(UUID userId);
    UserProfileDto updateCurrentUserProfile(UUID userId, UpdateUserProfileRequest request);
    void deleteUserProfile(UUID userId);
    void syncUser(CustomUserPrincipal principal);

    Page<UserProfileDto> getAllUsers(UserFilterRequest userFilterRequest);
}
