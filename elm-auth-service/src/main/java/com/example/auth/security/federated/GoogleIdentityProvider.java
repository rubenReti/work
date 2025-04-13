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
public class GoogleIdentityProvider implements FederatedIdentityProvider {

    private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public String extractEmail(String idToken) throws Exception {
        JWSObject jws = JWSObject.parse(idToken);

        JWKSet jwkSet = JWKSet.load(new URL(GOOGLE_JWKS_URI));
        JWK jwk = jwkSet.getKeyByKeyId(jws.getHeader().getKeyID());

        RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.toRSAKey().toPublicKey());
        if (!jws.verify(verifier)) {
            throw new IllegalArgumentException("Invalid Google ID token signature");
        }

        Map<String, Object> payload = jws.getPayload().toJSONObject();
        if (!Boolean.TRUE.equals(payload.get("email_verified"))) {
            throw new IllegalArgumentException("Google email not verified");
        }

        return (String) payload.get("email");
    }
}


//POST /auth/federated/login/google

//Google ID Token Payload (Decoded)
//{
//	  "iss": "https://accounts.google.com",
//	  "azp": "1234567890-abc123def456.apps.googleusercontent.com",
//	  "aud": "1234567890-abc123def456.apps.googleusercontent.com",
//	  "sub": "109237498237409283740923",
//	  "email": "john.doe@gmail.com",
//	  "email_verified": true,
//	  "name": "John Doe",
//	  "picture": "https://lh3.googleusercontent.com/a-/example123/photo.jpg",
//	  "given_name": "John",
//	  "family_name": "Doe",
//	  "locale": "en",
//	  "iat": 1712951273,
//	  "exp": 1712954873
//	}
