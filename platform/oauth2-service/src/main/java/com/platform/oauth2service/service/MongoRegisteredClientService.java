package com.platform.oauth2service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.oauth2service.document.MongoRegisteredClient;
import com.platform.oauth2service.repository.MongoRegisteredClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MongoRegisteredClientService implements RegisteredClientRepository {

    private final MongoRegisteredClientRepository repository;
    private final ObjectMapper objectMapper;

    public MongoRegisteredClientService(MongoRegisteredClientRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModules(org.springframework.security.jackson2.SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        this.objectMapper.registerModule(new org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        MongoRegisteredClient entity = toEntity(registeredClient);
        repository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return repository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return repository.findByClientId(clientId).map(this::toObject).orElse(null);
    }

    private MongoRegisteredClient toEntity(RegisteredClient client) {
        MongoRegisteredClient entity = new MongoRegisteredClient();
        entity.setId(client.getId());
        entity.setClientId(client.getClientId());
        entity.setClientIdIssuedAt(client.getClientIdIssuedAt());
        entity.setClientSecret(client.getClientSecret());
        entity.setClientSecretExpiresAt(client.getClientSecretExpiresAt());
        entity.setClientName(client.getClientName());
        entity.setClientAuthenticationMethods(client.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet()));
        entity.setAuthorizationGrantTypes(client.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).collect(Collectors.toSet()));
        entity.setRedirectUris(client.getRedirectUris());
        entity.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris());
        entity.setScopes(client.getScopes());

        try {
            entity.setClientSettingsJson(objectMapper.writeValueAsString(client.getClientSettings().getSettings()));
            entity.setTokenSettingsJson(objectMapper.writeValueAsString(client.getTokenSettings().getSettings()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return entity;
    }

    private RegisteredClient toObject(MongoRegisteredClient entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt())
                .clientName(entity.getClientName());

        if (entity.getClientAuthenticationMethods() != null) {
            entity.getClientAuthenticationMethods().forEach(method ->
                    builder.clientAuthenticationMethod(new ClientAuthenticationMethod(method)));
        }
        if (entity.getAuthorizationGrantTypes() != null) {
            entity.getAuthorizationGrantTypes().forEach(grantType ->
                    builder.authorizationGrantType(new AuthorizationGrantType(grantType)));
        }
        if (entity.getRedirectUris() != null) {
            entity.getRedirectUris().forEach(builder::redirectUri);
        }
        if (entity.getPostLogoutRedirectUris() != null) {
            entity.getPostLogoutRedirectUris().forEach(builder::postLogoutRedirectUri);
        }
        if (entity.getScopes() != null) {
            entity.getScopes().forEach(builder::scope);
        }

        try {
            if (entity.getClientSettingsJson() != null) {
                Map<String, Object> clientSettingsMap = objectMapper.readValue(entity.getClientSettingsJson(), new TypeReference<>() {});
                builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());
            }
            if (entity.getTokenSettingsJson() != null) {
                Map<String, Object> tokenSettingsMap = objectMapper.readValue(entity.getTokenSettingsJson(), new TypeReference<>() {});
                builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return builder.build();
    }
}

