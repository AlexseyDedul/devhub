package com.addh.ws.auth_service.infrastructure.security;

import com.addh.ws.auth_service.domain.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = "ead71fc94c76ee4bcb4ac267c54a87f7aef882e34f41ad3479a706cff8347b218002fba11b592e787493e73b3196ddbab87880936ff62bd1dca4eb5cd47fd5b5e4dc3bc144f414ef82ae25301e2eba5febe306de99cf383d45d96adba8f568d746d63bf84b75493740bd0b2c721def2d97dfd2a171756c93001f0e7d81f90be6a519d1b8ef2476e92e6e72744636f75e929d31c76011ba27bfd4b5d3e766ae9ff019ac8201c0024ea6f4e059ad11a74b837b4775ac64dbecdde2cf9946496681e38aae6fb706fed692ece68bc5dcb36fe92b946d5080f7f7006c9b6d3a19c2a6b4f0e97b56b4c2535d9748f7701dc82d741f954b3b5d38c53787ef9628db9a4f";
        long jwtExpirationMs = 3600000; // 1 hour
        long refreshExpirationMs = 604800000; // 7 days
        jwtService = new JwtService(secret, jwtExpirationMs, refreshExpirationMs);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
    }

    @Test
    void testExtractClaim() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin@example.com")
                .password("admin")
                .roles("ADMIN")
                .build();

        String token = jwtService.generateToken(Map.of("custom", "value"), userDetails);

        Claims claims = jwtService.extractClaim(token, c -> c);
        assertThat(claims.getSubject()).isEqualTo("admin@example.com");
        assertThat(claims.get("custom")).isEqualTo("value");
    }

    @Test
    void testGenerateRefreshToken() {
        User user = User.builder()
                .email("refresh@example.com")
                .build();

        String refreshToken = jwtService.generateRefreshToken(user);
        assertThat(refreshToken).isNotBlank();
        assertThat(jwtService.extractUsername(refreshToken)).isEqualTo("refresh@example.com");
    }
}