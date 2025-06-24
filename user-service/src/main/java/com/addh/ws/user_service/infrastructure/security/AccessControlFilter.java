package com.addh.ws.user_service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class AccessControlFilter extends OncePerRequestFilter {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    private static final List<String> ADMIN_ONLY_ENDPOINTS = List.of(
            "DELETE:/api/v1/users/{id}",
            "GET:/api/v1/users/{id}",
            "GET:/api/v1/users"
    );

    private static final List<String> IGNORED_ENDPOINTS = List.of(
            "GET:/api/v1/users/me",
            "PUT:/api/v1/users/me",
            "DELETE:/api/v1/users/me"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            String method = request.getMethod();
            String path = request.getRequestURI();

            if (IGNORED_ENDPOINTS.stream().anyMatch(rule -> rule.equals(method + ":" + path))) {
                filterChain.doFilter(request, response);
                return;
            }

            // Match against protected endpoints
            boolean adminOnly = ADMIN_ONLY_ENDPOINTS.stream()
                    .anyMatch(pattern -> {
                        String[] parts = pattern.split(":");
                        return method.equals(parts[0]) && matcher.match(parts[1], path);
                    });

            if (adminOnly && !principal.getRoles().contains("ADMIN")) {
                log.warn("Access denied for user {} to {} {}", principal.getUsername(), method, path);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
