package com.addh.ws.user_service.api.controller;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.domain.ports.UserProfileServicePort;
import com.addh.ws.user_service.infrastructure.security.annotation.IsAdmin;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileServicePort userProfileService;

    @IsAdmin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(id));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        UUID uuid = UUID.fromString(jwt.getClaimAsString("id"));
        return ResponseEntity.ok(userProfileService.getUserProfileById(uuid));
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateUserProfileRequest request
    ) {
        UUID uuid = UUID.fromString(jwt.getClaimAsString("id"));
        return ResponseEntity.ok(userProfileService.updateCurrentUserProfile(uuid, request));
    }

    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID uuid = UUID.fromString(jwt.getClaimAsString("id"));
        userProfileService.deleteUserProfile(uuid);
        return ResponseEntity.noContent().build();
    }
}
