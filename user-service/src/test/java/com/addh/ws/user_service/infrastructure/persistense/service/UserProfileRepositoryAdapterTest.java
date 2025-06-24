package com.addh.ws.user_service.infrastructure.persistense.service;

import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.domain.ports.UserProfileRepositoryPort;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import com.addh.ws.user_service.infrastructure.persistense.jpa.JpaUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class UserProfileRepositoryAdapterTest {
    @Autowired
    private UserProfileRepositoryPort repository;

    @Autowired
    private JpaUserProfileRepository jpaRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();

        userId = UUID.randomUUID();
        UserProfileEntity entity = UserProfileEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(List.of("USER"))
                .build();

        jpaRepository.save(entity);
    }

    @Test
    void findById_shouldReturnUserProfile() {
        Optional<UserProfile> result = repository.findById(userId);
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void save_shouldPersistUserProfile() {
        UserProfile profile = new UserProfile(
                UUID.randomUUID(),
                "newuser",
                "new@example.com",
                "New",
                "User",
                List.of("USER"),
                null,
                null,
                null,
                Instant.now()
        );

        UserProfile saved = repository.save(profile);
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(jpaRepository.findById(saved.getUserId())).isPresent();
    }

    @Test
    void existsById_shouldReturnTrue() {
        boolean exists = repository.existsById(userId);
        assertThat(exists).isTrue();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        repository.deleteById(userId);
        assertThat(jpaRepository.findById(userId)).isNotPresent();
    }

    @Test
    void findAll_shouldReturnPagedResult() {
        var page = repository.findAll(Specification.where(null), PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getUsername()).isEqualTo("testuser");
    }
}