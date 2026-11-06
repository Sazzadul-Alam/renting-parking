package com.platform.oauth2service.service;

import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.platform.oauth2service.document.JwkKeyDocument;
import com.platform.oauth2service.repository.JwkKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwkKeyService {

    private static final Logger log = LoggerFactory.getLogger(JwkKeyService.class);
    private static final String DEFAULT_KEY_ID = "platform-auth-rsa-key";

    private final JwkKeyRepository jwkKeyRepository;
    private volatile RSAKey rsaKey;

    public JwkKeyService(JwkKeyRepository jwkKeyRepository) {
        this.jwkKeyRepository = jwkKeyRepository;
    }

    public synchronized RSAKey getOrCreateRsaKey() {
        if (this.rsaKey != null) {
            return this.rsaKey;
        }

        Optional<JwkKeyDocument> existing = jwkKeyRepository.findByKeyId(DEFAULT_KEY_ID);
        if (existing.isPresent() && existing.get().getJwkJson() != null) {
            try {
                log.info("Loading existing RSA key from MongoDB Atlas with keyId: {}", DEFAULT_KEY_ID);
                this.rsaKey = RSAKey.parse(existing.get().getJwkJson());
                return this.rsaKey;
            } catch (Exception ex) {
                log.warn("Failed to parse existing JWK JSON, will regenerate: {}", ex.getMessage());
            }
        }

        log.info("Generating new 2048-bit RSA key for token signing...");
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();

            RSAKey rsaJwk = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(DEFAULT_KEY_ID)
                    .build();

            JwkKeyDocument doc = existing.orElseGet(JwkKeyDocument::new);
            if (doc.getId() == null) {
                doc.setId(UUID.randomUUID().toString());
            }
            doc.setKeyId(DEFAULT_KEY_ID);
            doc.setJwkJson(rsaJwk.toJSONString());
            doc.setCreatedAt(Instant.now());

            jwkKeyRepository.save(doc);
            log.info("RSA key saved successfully to MongoDB Atlas.");
            this.rsaKey = rsaJwk;
            return this.rsaKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate and persist RSA key", e);
        }
    }
}
