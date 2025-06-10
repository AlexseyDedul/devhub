package com.addh.ws.auth_service.api.dto;

import com.addh.ws.auth_service.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;

    public static UserProfileDto fromUser(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(role -> role.getRoleType().name())
                        .toList()
        );
    }
}
