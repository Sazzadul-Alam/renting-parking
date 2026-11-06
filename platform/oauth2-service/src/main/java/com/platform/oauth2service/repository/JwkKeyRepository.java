package com.platform.oauth2service.repository;

import com.platform.oauth2service.document.JwkKeyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwkKeyRepository extends MongoRepository<JwkKeyDocument, String> {
    Optional<JwkKeyDocument> findByKeyId(String keyId);
}

