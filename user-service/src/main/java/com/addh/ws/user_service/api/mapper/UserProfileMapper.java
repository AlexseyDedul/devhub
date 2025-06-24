package com.addh.ws.user_service.api.mapper;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.domain.model.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "roles", source = "roles")
    UserProfileDto toDto(UserProfile domain);

    @Mapping(target = "roles", source = "roles")
    UserProfile toDomain(UserProfileDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDomainFromRequest(UpdateUserProfileRequest request, @MappingTarget UserProfile domain);
}
