package com.addh.ws.auth_service.infrastructure.persistence.service;

import com.addh.ws.auth_service.domain.enums.TokenType;
import com.addh.ws.auth_service.domain.model.Token;
import com.addh.ws.auth_service.domain.model.User;
import com.addh.ws.auth_service.domain.ports.TokenRepositoryPort;
import com.addh.ws.auth_service.domain.ports.TokenServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceAdapter implements TokenServicePort {
    private final TokenRepositoryPort tokenRepository;

    @Override
    public void saveUserToken(User user, String jwtToken, TokenType tokenType) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public void revokeAllUserTokens(User user) {
        var validAccessTokens = tokenRepository.findAllValidTokensByUserId(user.getId());

        if (validAccessTokens.isEmpty()) return;

        validAccessTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validAccessTokens);
    }

    @Override
    public boolean isTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
