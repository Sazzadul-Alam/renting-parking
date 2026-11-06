package com.platform.oauth2service.repository;

import com.platform.oauth2service.document.MongoOAuth2Authorization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoOAuth2AuthorizationRepository extends MongoRepository<MongoOAuth2Authorization, String> {
    Optional<MongoOAuth2Authorization> findByState(String state);
    Optional<MongoOAuth2Authorization> findByAuthorizationCodeValue(String authorizationCode);
    Optional<MongoOAuth2Authorization> findByAccessTokenValue(String accessToken);
    Optional<MongoOAuth2Authorization> findByRefreshTokenValue(String refreshToken);
    Optional<MongoOAuth2Authorization> findByOidcIdTokenValue(String idToken);
    Optional<MongoOAuth2Authorization> findByUserCodeValue(String userCode);
    Optional<MongoOAuth2Authorization> findByDeviceCodeValue(String deviceCode);
}

