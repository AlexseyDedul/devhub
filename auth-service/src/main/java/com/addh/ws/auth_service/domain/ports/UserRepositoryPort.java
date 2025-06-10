package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
}
