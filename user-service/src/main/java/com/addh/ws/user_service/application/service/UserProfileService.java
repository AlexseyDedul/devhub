package com.addh.ws.user_service.application.service;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.api.filter.UserFilterRequest;
import com.addh.ws.user_service.api.mapper.UserProfileMapper;
import com.addh.ws.user_service.domain.exception.UserProfileNotFoundException;
import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.domain.ports.UserProfileRepositoryPort;
import com.addh.ws.user_service.domain.ports.UserProfileServicePort;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import com.addh.ws.user_service.infrastructure.security.CustomUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
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

    @Override
    public void syncUser(CustomUserPrincipal principal) {
        log.info("Syncing user profile");
        log.info("User : {}", principal);

        repository.findById(principal.getUserId())
                .map(user -> {
                    user.setEmail(principal.getEmail());
                    user.setFirstName(principal.getFirstName());
                    user.setLastName(principal.getLastName());
                    user.setUsername(principal.getUsername());
                    user.setRoles(principal.getRoles());
                    user.setUpdatedAt(Instant.now());
                    log.info("User : {}", user);
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    UserProfile newUser = new UserProfile();
                    newUser.setUserId(principal.getUserId());
                    newUser.setUsername(principal.getUsername());
                    newUser.setEmail(principal.getEmail());
                    newUser.setFirstName(principal.getFirstName());
                    newUser.setLastName(principal.getLastName());
                    newUser.setRoles(principal.getRoles());
                    newUser.setUpdatedAt(Instant.now());
                    return repository.save(newUser);
                });
    }

    @Override
    public Page<UserProfileDto> getAllUsers(UserFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                Sort.by(Sort.Direction.fromString(
                        Optional.ofNullable(filterRequest.getDirection()).orElse("ASC")
                ), Optional.ofNullable(filterRequest.getSortBy()).orElse("updatedAt"))
        );

        Specification<UserProfileEntity> spec = Specification.where(null);

        if (filterRequest.getEmail() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + filterRequest.getEmail().toLowerCase() + "%"));
        if (filterRequest.getUsername() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + filterRequest.getUsername().toLowerCase() + "%"));
        if (filterRequest.getFirstName() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + filterRequest.getFirstName().toLowerCase() + "%"));
        if (filterRequest.getLastName() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + filterRequest.getLastName().toLowerCase() + "%"));
        if (filterRequest.getRole() != null)
            spec = spec.and((root, query, cb) -> cb.isMember(filterRequest.getRole(), root.get("roles")));

        return repository.findAll(spec, pageable)
                .map(mapper::toDto);
    }
}
