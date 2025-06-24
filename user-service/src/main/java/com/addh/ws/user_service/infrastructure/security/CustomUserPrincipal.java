package com.addh.ws.user_service.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
