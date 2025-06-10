package com.addh.ws.user_service.api.mapper;

import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileEntityMapper {
    UserProfile toDomain(UserProfileEntity entity);
    UserProfileEntity toEntity(UserProfile domain);
}
