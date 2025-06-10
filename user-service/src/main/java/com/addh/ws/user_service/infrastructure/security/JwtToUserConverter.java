package com.addh.ws.user_service.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class JwtToUserConverter implements Converter<Jwt, JwtAuthenticationToken> {

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("id"));
        log.info("JWT Claims : {}", jwt.getClaims());
        log.info("JWT id : {}", userId);
        var authorities = jwt.getClaimAsStringList("roles");
        log.info("JWT roles : {}", authorities);
        if (authorities == null) {
            authorities = List.of();
        }

        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new JwtAuthenticationToken(jwt, grantedAuthorities, userId.toString());
    }

}
