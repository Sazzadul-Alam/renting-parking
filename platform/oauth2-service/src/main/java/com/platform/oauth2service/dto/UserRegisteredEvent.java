package com.platform.oauth2service.dto;

import java.time.Instant;

public class UserRegisteredEvent {
    private String userId;
    private String username;
    private String email;
    private Instant createdAt;

    public UserRegisteredEvent() {}

    public UserRegisteredEvent(String userId, String username, String email, Instant createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

