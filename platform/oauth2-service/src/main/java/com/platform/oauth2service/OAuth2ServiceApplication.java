package com.platform.oauth2service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OAuth2ServiceApplication {
    public static void main(String[] args) {
        String mongoUri = System.getenv("MONGODB_URI");
        if (mongoUri == null || mongoUri.isBlank()) {
            System.err.println("FATAL ERROR: MONGODB_URI environment variable is not set. Please set MONGODB_URI before starting oauth2-service.");
            System.exit(1);
        }
        SpringApplication.run(OAuth2ServiceApplication.class, args);
    }
}

