package com.example.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // 🔥 **Ensure Strong Secret Key**
    private static final String SECRET = "ThisIsMySuperLongSecretKeyForJWTValidation12345678901234567890";

    // ✅ **Ensure Secret Key is Correctly Encoded**
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes(StandardCharsets.UTF_8)));

    // 🔥 **Make JWT Valid for 10 Years**
    private static final long EXPIRATION = 10L * 365 * 24 * 60 * 60 * 1000; // 10 years in milliseconds

    public String generateToken(String email, List<String> roles) {
        System.out.println("🔍 Generating JWT Token for: " + email);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 🔥 10-year expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 🔥 Make sure this matches verification
                .compact();
    }

    public Claims extractClaims(String token) {
        System.out.println("🔍 Extracting claims from JWT...");
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            System.out.println("✅ Token is valid!");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }
}
