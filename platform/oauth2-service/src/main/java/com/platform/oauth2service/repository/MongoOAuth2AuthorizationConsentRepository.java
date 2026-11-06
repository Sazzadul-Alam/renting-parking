package com.platform.oauth2service.repository;

import com.platform.oauth2service.document.MongoOAuth2AuthorizationConsent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoOAuth2AuthorizationConsentRepository extends MongoRepository<MongoOAuth2AuthorizationConsent, String> {
    Optional<MongoOAuth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}

