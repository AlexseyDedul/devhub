package com.addh.ws.auth_service.infrastructure.persistence.jpa;

import com.addh.ws.auth_service.domain.model.Token;
import com.addh.ws.auth_service.domain.ports.TokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepositoryPort {
    private final JpaTokenRepository jpaTokenRepository;

    @Override
    public List<Token> findAllValidTokensByUserId(UUID userId) {
        return jpaTokenRepository.findAllValidTokensByUserId(userId);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return jpaTokenRepository.findByToken(token);
    }

    @Override
    public Token save(Token token) {
        return jpaTokenRepository.save(token);
    }

    @Override
    public void saveAll(List<Token> validTokens) {
        jpaTokenRepository.saveAll(validTokens);
    }
}
