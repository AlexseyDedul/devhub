package com.addh.ws.user_service.domain.exception;

import java.util.UUID;

public class UserProfileNotFoundException extends RuntimeException {
  public UserProfileNotFoundException(UUID userId) {
    super("User profile not found for userId: " + userId);
  }
}
