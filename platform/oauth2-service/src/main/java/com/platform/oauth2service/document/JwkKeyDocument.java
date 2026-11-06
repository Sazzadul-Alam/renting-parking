package com.platform.oauth2service.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "jwk_keys")
public class JwkKeyDocument {
    @Id
    private String id; // Key ID
    private String keyId;
    private String jwkJson; // Complete Nimbus RSAKey JSON representation
    private Instant createdAt = Instant.now();

    public JwkKeyDocument() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getJwkJson() {
        return jwkJson;
    }

    public void setJwkJson(String jwkJson) {
        this.jwkJson = jwkJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
