package com.example.auth.security;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@Component
public class GoogleTokenVerifier {

    private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";

    public String extractEmailFromToken(String idToken) throws Exception {
        JWSObject jws = JWSObject.parse(idToken);  					// Splits the token into: Header, Payload, Signature

        // Download Google's public keys
        JWKSet jwkSet = JWKSet.load(new URL(GOOGLE_JWKS_URI));  	//Gets a live list of public keys
        JWK jwk = jwkSet.getKeyByKeyId(jws.getHeader().getKeyID());	// Finds the matching key in the JWKS list

        // Validate the token signature - Confirms token was signed by Google
        RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.toRSAKey().toPublicKey());
        if (!jws.verify(verifier)) {
            throw new IllegalArgumentException("Invalid Google ID token signature");
        }

        // Parse payload and return the email
        Map<String, Object> payload = jws.getPayload().toJSONObject();
        if (!Boolean.TRUE.equals(payload.get("email_verified"))) {   //Confirms Google has verified this user owns the email
            throw new IllegalArgumentException("Google email not verified");
        }

        return (String) payload.get("email");
    }
}
