package com.addh.ws.user_service.infrastructure.persistense.jpa;

import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaUserProfileRepository extends JpaRepository<UserProfileEntity, UUID>, JpaSpecificationExecutor<UserProfileEntity> {
}
