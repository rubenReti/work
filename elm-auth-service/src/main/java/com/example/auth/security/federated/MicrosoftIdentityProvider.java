package com.example.auth.security.federated;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@Component
public class MicrosoftIdentityProvider implements FederatedIdentityProvider {

    private static final String MICROSOFT_JWKS_URI = "https://login.microsoftonline.com/common/discovery/v2.0/keys";

    @Override
    public String getProviderName() {
        return "microsoft";
    }

    @Override
    public String extractEmail(String idToken) throws Exception {
        JWSObject jws = JWSObject.parse(idToken);

        JWKSet jwkSet = JWKSet.load(new URL(MICROSOFT_JWKS_URI));
        JWK jwk = jwkSet.getKeyByKeyId(jws.getHeader().getKeyID());

        RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.toRSAKey().toPublicKey());
        if (!jws.verify(verifier)) {
            throw new IllegalArgumentException("Invalid Microsoft ID token signature");
        }

        Map<String, Object> payload = jws.getPayload().toJSONObject();

        // Email might be missing, fallback to preferred_username
        String email = (String) payload.get("email");
        if (email == null) {
            email = (String) payload.get("preferred_username");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email not found in Microsoft token");
        }

        return email;
    }
}


//POST /auth/federated/login/microsoft → goes to MicrosoftIdentityProvider
//
//Microsoft ID Token Example Payload:
//{
//	  "iss": "https://login.microsoftonline.com/<tenant_id>/v2.0",
//	  "aud": "<your-client-id>",
//	  "sub": "abc123...",
//	  "preferred_username": "john.doe@company.com",
//	  "name": "John Doe",
//	  "email": "john.doe@company.com"
//	}
//
