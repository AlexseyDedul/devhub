package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.domain.enums.TokenType;
import com.addh.ws.auth_service.domain.model.Token;
import com.addh.ws.auth_service.domain.model.User;

import java.util.Optional;

public interface TokenServicePort {
    void saveUserToken(User user, String token, TokenType tokenType);
    void revokeAllUserTokens(User user);
    boolean isTokenValid(String token);
    Optional<Token> findByToken(String token);
}
