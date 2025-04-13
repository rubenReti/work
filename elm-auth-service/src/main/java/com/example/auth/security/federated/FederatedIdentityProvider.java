package com.example.auth.security.federated;

public interface FederatedIdentityProvider {
    String getProviderName(); // "google", "microsoft", etc.
    String extractEmail(String idToken) throws Exception;
}
