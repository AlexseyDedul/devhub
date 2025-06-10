package com.addh.ws.auth_service.infrastructure.init;

import com.addh.ws.auth_service.domain.enums.RoleType;
import com.addh.ws.auth_service.domain.model.Role;
import com.addh.ws.auth_service.infrastructure.persistence.jpa.JpaRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final JpaRoleRepository jpaRoleRepository;

    @PostConstruct
    public void init() {
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            boolean exists = jpaRoleRepository.findByRoleType(roleType).isPresent();
            if (!exists) {
                jpaRoleRepository.save(Role.builder().roleType(roleType).build());
            }
        });
    }
}
