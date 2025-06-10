package com.addh.ws.user_service.domain.ports;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;

import java.util.UUID;

public interface UserProfileServicePort {
    UserProfileDto getUserProfileById(UUID userId);
    UserProfileDto updateCurrentUserProfile(UUID userId, UpdateUserProfileRequest request);
    void deleteUserProfile(UUID userId);
}
