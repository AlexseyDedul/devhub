package com.addh.ws.user_service.infrastructure.persistense.service;

import com.addh.ws.user_service.api.mapper.UserProfileEntityMapper;
import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.domain.ports.UserProfileRepositoryPort;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import com.addh.ws.user_service.infrastructure.persistense.jpa.JpaUserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileRepositoryAdapter implements UserProfileRepositoryPort {
    private final JpaUserProfileRepository jpaRepository;
    private final UserProfileEntityMapper mapper;

    @Override
    public Optional<UserProfile> findById(UUID userId) {
        return jpaRepository.findById(userId).map(mapper::toDomain);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfileEntity entity = mapper.toEntity(profile);
        log.info("Saving user profile {}", entity);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsById(UUID userId) {
        return jpaRepository.existsById(userId);
    }

    @Override
    public void deleteById(UUID userId) {
        jpaRepository.deleteById(userId);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public Page<UserProfile> findAll(Specification<UserProfileEntity> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }
}
