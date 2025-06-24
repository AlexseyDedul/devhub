package com.addh.ws.user_service.domain.ports;

import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepositoryPort {
    Optional<UserProfile> findById(UUID userId);
    UserProfile save(UserProfile profile);
    boolean existsById(UUID userId);
    void deleteById(UUID userId);

    void deleteAll();

    Page<UserProfile> findAll(Specification<UserProfileEntity> spec, Pageable pageable);
}
