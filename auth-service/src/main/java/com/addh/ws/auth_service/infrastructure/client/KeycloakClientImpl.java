package com.addh.ws.auth_service.infrastructure.client;

import com.addh.ws.auth_service.api.dto.*;
import com.addh.ws.auth_service.domain.ports.KeycloakClientPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakClientImpl implements KeycloakClientPort {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.admin-user-create-uri}")
    private String userCreateUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.userinfo-uri}")
    private String userInfoUri;

    @Value("${keycloak.admin-login}")
    private String adminLogin;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${keycloak.roles-uri}")
    private String rolesUri;

    @Value("${keycloak.roles-mapping-uri}")
    private String rolesMappingUri;

    private static final List<String> roles = List.of("USER", "view-profile", "manage-account");

    @Override
    public void createUser(RegisterRequest request) {
        AuthResponse adminCli = getAdminCli();

        Map<String, Object> payload = Map.of(
                "username", request.username(),
                "email", request.email(),
                "firstName", request.firstName(),
                "lastName", request.lastName(),
                "enabled", true,
                "emailVerified", true,
                "credentials", new Object[] {
                        Map.of("type", "password",
                                "value", request.password(),
                                "temporary", false)
                }
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminCli.accessToken());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(userCreateUri, entity, Void.class);

        URI location = response.getHeaders().getLocation();
        if (location == null) throw new RuntimeException("User creation failed - no location header");
        String[] segments = location.getPath().split("/");
        String userId = segments[segments.length - 1];

        assignRealmRoles(userId, roles);
    }

    @Override
    public AuthResponse getToken(LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("username", request.username());
        form.add("password", request.password());
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", "openid profile email");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        return getAuthenticationEntity(entity);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", request.refreshToken());
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        return getAuthenticationEntity(entity);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
        String token = authHeader.substring(7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var uri = UriComponentsBuilder.fromUriString(tokenUri.replace("token", "logout"))
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("refresh_token", token)
                .build().toUri();

        restTemplate.postForEntity(uri, null, Void.class);
    }

    @Override
    public UserProfileDto getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new RuntimeException("Unauthorized");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authHeader.substring(7));

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        JsonNode json = parse(response.getBody());

        List<String> roles = new ArrayList<>();
        if (json.has("roles") && json.get("roles").isArray()) {
            json.get("roles").forEach(role -> roles.add(role.asText()));
        }

        return UserProfileDto.builder()
                .id(json.get("sub").asText())
                .username(json.get("preferred_username").asText())
                .email(json.get("email").asText())
                .firstName(json.has("given_name") ? json.get("given_name").asText() : null)
                .lastName(json.has("family_name") ? json.get("family_name").asText() : null)
                .roles(roles)
                .build();
    }

    private JsonNode parse(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("Invalid response format", e);
        }
    }

    private AuthResponse getAdminCli(){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminLogin);
        form.add("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        return getAuthenticationEntity(entity);
    }

    private AuthResponse getAuthenticationEntity(HttpEntity<MultiValueMap<String, String>> form) {
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, form, String.class);

        JsonNode json = parse(response.getBody());
        return AuthResponse.builder()
                .accessToken(json.get("access_token").asText())
                .refreshToken(json.get("refresh_token").asText())
                .expiresIn(json.get("expires_in").asLong())
                .tokenType(json.get("token_type").asText())
                .build();
    }

    private void assignRealmRoles(String userId, List<String> roles) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminCli().accessToken());

        // Get roles
        String roleListUri = String.format(rolesUri);

        ResponseEntity<JsonNode> roleListResp = restTemplate.exchange(
                roleListUri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class
        );

        List<Map<String, Object>> rolesToAssign = new ArrayList<>();
        for (JsonNode roleNode : roleListResp.getBody()) {
            if (roles.contains(roleNode.get("name").asText())) {
                rolesToAssign.add(Map.of(
                        "id", roleNode.get("id").asText(),
                        "name", roleNode.get("name").asText()
                ));
            }
        }

        String assignUri = String.format(rolesMappingUri, userId);
        HttpEntity<Object> assignEntity = new HttpEntity<>(rolesToAssign, headers);
        restTemplate.postForEntity(assignUri, assignEntity, Void.class);
    }

}
