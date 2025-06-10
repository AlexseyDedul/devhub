package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.domain.enums.RoleType;
import com.addh.ws.auth_service.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(RoleType roleType);
    Role save(Role role);
}
