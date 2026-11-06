package com.platform.oauth2service.service;

import com.platform.oauth2service.document.MongoOAuth2AuthorizationConsent;
import com.platform.oauth2service.repository.MongoOAuth2AuthorizationConsentRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MongoOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final MongoOAuth2AuthorizationConsentRepository repository;
    private final RegisteredClientRepository registeredClientRepository;

    public MongoOAuth2AuthorizationConsentService(MongoOAuth2AuthorizationConsentRepository repository,
                                                  RegisteredClientRepository registeredClientRepository) {
        this.repository = repository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        MongoOAuth2AuthorizationConsent entity = toEntity(authorizationConsent);
        repository.findByRegisteredClientIdAndPrincipalName(entity.getRegisteredClientId(), entity.getPrincipalName())
                .ifPresent(existing -> entity.setId(existing.getId()));
        repository.save(entity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        repository.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(),
                authorizationConsent.getPrincipalName()
        );
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return repository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)
                .map(this::toObject)
                .orElse(null);
    }

    private MongoOAuth2AuthorizationConsent toEntity(OAuth2AuthorizationConsent consent) {
        Set<String> authorities = consent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return new MongoOAuth2AuthorizationConsent(
                consent.getRegisteredClientId(),
                consent.getPrincipalName(),
                authorities
        );
    }

    private OAuth2AuthorizationConsent toObject(MongoOAuth2AuthorizationConsent entity) {
        RegisteredClient registeredClient = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                entity.getRegisteredClientId(), entity.getPrincipalName());
        if (entity.getAuthorities() != null) {
            for (String authority : entity.getAuthorities()) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }
        return builder.build();
    }
}

