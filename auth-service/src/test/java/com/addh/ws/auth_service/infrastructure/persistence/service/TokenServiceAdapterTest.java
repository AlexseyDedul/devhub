package com.addh.ws.auth_service.infrastructure.persistence.service;

import com.addh.ws.auth_service.domain.enums.TokenType;
import com.addh.ws.auth_service.domain.model.Token;
import com.addh.ws.auth_service.domain.model.User;
import com.addh.ws.auth_service.domain.ports.TokenRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceAdapterTest {

    @Mock
    private TokenRepositoryPort tokenRepositoryPort;

    @InjectMocks
    private TokenServiceAdapter tokenServiceAdapter;

    @Test
    void saveUserToken_shouldSaveCorrectToken() {
        User user = User.builder().id(UUID.randomUUID()).build();
        String jwt = "test.jwt.token";

        tokenServiceAdapter.saveUserToken(user, jwt, TokenType.ACCESS_TOKEN);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepositoryPort).save(tokenCaptor.capture());

        Token savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getToken()).isEqualTo(jwt);
        assertThat(savedToken.getTokenType()).isEqualTo(TokenType.ACCESS_TOKEN);
        assertThat(savedToken.isExpired()).isFalse();
        assertThat(savedToken.isRevoked()).isFalse();
    }

    @Test
    void revokeAllUserTokens_shouldMarkTokensAsRevokedAndExpired() {
        UUID uuid = UUID.randomUUID();
        User user = User.builder().id(uuid).build();
        List<Token> tokens = List.of(
                Token.builder()
                        .id(UUID.randomUUID())
                        .tokenType(TokenType.ACCESS_TOKEN)
                        .expired(false)
                        .revoked(false)
                        .build(),
                Token.builder()
                        .id(UUID.randomUUID())
                        .tokenType(TokenType.ACCESS_TOKEN)
                        .expired(false)
                        .revoked(false)
                        .build()
        );

        when(tokenRepositoryPort.findAllValidTokensByUserId(user.getId())).thenReturn(tokens);

        tokenServiceAdapter.revokeAllUserTokens(user);

        assertThat(tokens).allMatch(t -> t.isRevoked() && t.isExpired());
        verify(tokenRepositoryPort, times(1)).saveAll(tokens);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String tokenStr = "token";
        Token token= Token.builder()
                .id(UUID.randomUUID())
                .tokenType(TokenType.ACCESS_TOKEN)
                .expired(false)
                .revoked(false)
                .build();
        Optional<Token> tokenOptional = Optional.of(token);
        when(tokenRepositoryPort.findByToken(tokenStr)).thenReturn(tokenOptional);

        assertThat(tokenServiceAdapter.isTokenValid(tokenStr)).isTrue();
    }
}