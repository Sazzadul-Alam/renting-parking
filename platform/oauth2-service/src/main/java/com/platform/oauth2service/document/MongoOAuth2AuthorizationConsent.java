package com.platform.oauth2service.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "oauth2_authorization_consents")
@CompoundIndex(name = "client_principal_idx", def = "{'registeredClientId': 1, 'principalName': 1}", unique = true)
public class MongoOAuth2AuthorizationConsent {
    @Id
    private String id;
    private String registeredClientId;
    private String principalName;
    private Set<String> authorities;

    public MongoOAuth2AuthorizationConsent() {}

    public MongoOAuth2AuthorizationConsent(String registeredClientId, String principalName, Set<String> authorities) {
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.authorities = authorities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}

