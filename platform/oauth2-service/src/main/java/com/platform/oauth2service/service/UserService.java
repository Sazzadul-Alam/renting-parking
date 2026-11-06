package com.platform.oauth2service.service;

import com.platform.oauth2service.config.RabbitMQConfig;
import com.platform.oauth2service.document.User;
import com.platform.oauth2service.dto.RegisterRequest;
import com.platform.oauth2service.dto.UserRegisteredEvent;
import com.platform.oauth2service.dto.UserResponse;
import com.platform.oauth2service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());

        User saved = userRepository.save(user);
        log.info("Registered new user: {} (id: {})", saved.getUsername(), saved.getId());

        UserRegisteredEvent event = new UserRegisteredEvent(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getCreatedAt()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_USER_REGISTERED,
                    event
            );
            log.info("Published user.registered event for user {}", saved.getUsername());
        } catch (Exception e) {
            log.error("Failed to publish RabbitMQ event for user: {}", saved.getUsername(), e);
        }

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRoles(), saved.getCreatedAt());
    }
}

