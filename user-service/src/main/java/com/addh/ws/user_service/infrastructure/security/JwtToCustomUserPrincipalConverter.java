package com.addh.ws.user_service.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class JwtToCustomUserPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        UUID id = UUID.fromString(jwt.getSubject());
        String username = (String) claims.get("preferred_username");
        String email = (String) claims.get("email");
        String firstName = (String) claims.get("given_name");
        String lastName = (String) claims.get("family_name");

        List<String> roles = extractRoles(jwt);
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        CustomUserPrincipal principal = new CustomUserPrincipal(
                id,
                username,
                email,
                firstName,
                lastName,
                roles,
                claims,
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "n/a", authorities);
    }

    private List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }
        return ((Collection<String>) realmAccess.get("roles"))
                .stream().collect(Collectors.toList());
    }
}
