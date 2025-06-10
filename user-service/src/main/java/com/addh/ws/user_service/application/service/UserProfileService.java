package com.addh.ws.user_service.application.service;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.api.mapper.UserProfileMapper;
import com.addh.ws.user_service.domain.exception.UserProfileNotFoundException;
import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.domain.ports.UserProfileRepositoryPort;
import com.addh.ws.user_service.domain.ports.UserProfileServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserProfileService implements UserProfileServicePort {
    private final UserProfileRepositoryPort repository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileById(UUID userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));
        return mapper.toDto(profile);
    }

    @Override
    @Transactional
    public UserProfileDto updateCurrentUserProfile(UUID userId, UpdateUserProfileRequest request) {
        UserProfile profile = repository.findById(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .userId(userId)
                            .createdAt(Instant.now())
                            .build();
                    return repository.save(newProfile);
                });

        mapper.updateDomainFromRequest(request, profile);
        profile.setUpdatedAt(Instant.now());

        UserProfile updated = repository.save(profile);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUserProfile(UUID userId) {
        repository.deleteById(userId);
    }
}
