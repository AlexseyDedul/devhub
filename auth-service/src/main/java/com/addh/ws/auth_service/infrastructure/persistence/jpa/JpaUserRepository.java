package com.addh.ws.auth_service.infrastructure.persistence.jpa;

import com.addh.ws.auth_service.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
