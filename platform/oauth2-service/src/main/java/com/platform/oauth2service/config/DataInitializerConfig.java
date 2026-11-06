package com.platform.oauth2service.config;

import com.platform.oauth2service.document.User;
import com.platform.oauth2service.repository.UserRepository;
import com.platform.oauth2service.service.JwkKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Configuration
public class DataInitializerConfig {

    private static final Logger log = LoggerFactory.getLogger(DataInitializerConfig.class);

    @Bean
    public ApplicationRunner initializeData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RegisteredClientRepository registeredClientRepository,
            JwkKeyService jwkKeyService) {
        return args -> {
            log.info("Initializing OAuth2 / User data if missing...");

            // 1. Ensure RSA Key is created and saved
            jwkKeyService.getOrCreateRsaKey();

            // 2. Seed Admin user if missing
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                        UUID.randomUUID().toString(),
                        "admin",
                        "admin@platform.local",
                        passwordEncoder.encode("admin123"),
                        Set.of("ROLE_ADMIN", "ROLE_USER"),
                        true,
                        Instant.now()
                );
                userRepository.save(admin);
                log.info("Seeded default user: admin");
            }

            // 3. Seed Normal user if missing
            if (!userRepository.existsByUsername("user")) {
                User normalUser = new User(
                        UUID.randomUUID().toString(),
                        "user",
                        "user@platform.local",
                        passwordEncoder.encode("user123"),
                        Set.of("ROLE_USER"),
                        true,
                        Instant.now()
                );
                userRepository.save(normalUser);
                log.info("Seeded default user: user");
            }

            // 4. Seed Registered Client 'angular-client' if missing
            RegisteredClient existingClient = registeredClientRepository.findByClientId("angular-client");
            if (existingClient == null) {
                RegisteredClient angularClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("angular-client")
                        .clientName("Angular Frontend Client")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://localhost:4200/")
                        .redirectUri("http://localhost:4200/index.html")
                        .postLogoutRedirectUri("http://localhost:4200/")
                        .scope("openid")
                        .scope("profile")
                        .scope("api.read")
                        .scope("api.write")
                        .clientSettings(ClientSettings.builder()
                                .requireProofKey(true)
                                .requireAuthorizationConsent(false)
                                .build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(15))
                                .refreshTokenTimeToLive(Duration.ofHours(8))
                                .reuseRefreshTokens(true)
                                .build())
                        .build();

                registeredClientRepository.save(angularClient);
                log.info("Seeded registered OAuth2 client: angular-client");
            }
        };
    }
}

