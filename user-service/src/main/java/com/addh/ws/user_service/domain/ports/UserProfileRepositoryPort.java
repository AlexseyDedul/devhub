package com.addh.ws.user_service.domain.ports;

import com.addh.ws.user_service.domain.model.UserProfile;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepositoryPort {
    Optional<UserProfile> findById(UUID userId);
    UserProfile save(UserProfile profile);
    boolean existsById(UUID userId);
    void deleteById(UUID userId);

    void deleteAll();
}
