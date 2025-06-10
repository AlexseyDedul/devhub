package com.addh.ws.auth_service.infrastructure.persistence.jpa;

import com.addh.ws.auth_service.domain.enums.RoleType;
import com.addh.ws.auth_service.domain.model.Role;
import com.addh.ws.auth_service.domain.ports.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public Optional<Role> findByName(RoleType roleType) {
        return jpaRoleRepository.findByRoleType(roleType);
    }

    @Override
    public Role save(Role role) {
        return jpaRoleRepository.save(role);
    }
}
