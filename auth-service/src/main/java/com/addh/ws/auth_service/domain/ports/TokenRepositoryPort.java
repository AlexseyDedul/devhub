package com.addh.ws.auth_service.domain.ports;

import com.addh.ws.auth_service.domain.model.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepositoryPort {
    List<Token> findAllValidTokensByUserId(UUID userId);
    Optional<Token> findByToken(String token);
    Token save(Token token);
    void saveAll(List<Token> validTokens);
}
