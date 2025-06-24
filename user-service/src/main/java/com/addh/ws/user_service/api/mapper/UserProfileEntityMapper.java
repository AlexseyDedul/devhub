package com.addh.ws.user_service.api.mapper;

import com.addh.ws.user_service.domain.model.UserProfile;
import com.addh.ws.user_service.infrastructure.persistense.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileEntityMapper {
    @Mapping(target = "roles", source = "roles")
    UserProfile toDomain(UserProfileEntity entity);

    @Mapping(target = "roles", source = "roles")
    UserProfileEntity toEntity(UserProfile domain);
}
