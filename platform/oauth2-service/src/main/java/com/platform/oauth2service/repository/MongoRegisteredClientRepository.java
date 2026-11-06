package com.platform.oauth2service.repository;

import com.platform.oauth2service.document.MongoRegisteredClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoRegisteredClientRepository extends MongoRepository<MongoRegisteredClient, String> {
    Optional<MongoRegisteredClient> findByClientId(String clientId);
}

