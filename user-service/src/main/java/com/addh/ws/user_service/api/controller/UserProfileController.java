package com.addh.ws.user_service.api.controller;

import com.addh.ws.user_service.api.dto.UpdateUserProfileRequest;
import com.addh.ws.user_service.api.dto.UserProfileDto;
import com.addh.ws.user_service.api.filter.UserFilterRequest;
import com.addh.ws.user_service.domain.ports.UserProfileServicePort;
import com.addh.ws.user_service.infrastructure.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileServicePort userProfileService;

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(UserFilterRequest userFilterRequest) {
        return ResponseEntity.ok(userProfileService.getAllUsers(userFilterRequest));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(principal.getUserId()));
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> updateMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(userProfileService.updateCurrentUserProfile(principal.getUserId(), request));
    }

    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        userProfileService.deleteUserProfile(principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
