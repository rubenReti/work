
package com.example.auth.security;

import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;



@Component
public class JwtUtil {

 @Value("${auth.jwt.private-key}")
 private String privateKeyPem;

 @Value("${auth.jwt.public-key}")
 private String publicKeyPem;

 private RSAPublicKey publicKey;
 private RSAPrivateKey privateKey;

 private static final long EXPIRATION = 10L * 365 * 24 * 60 * 60 * 1000;

 @PostConstruct
 public void initKeys() {
     try {
         this.privateKey = getPrivateKeyFromPem(privateKeyPem);
         this.publicKey = getPublicKeyFromPem(publicKeyPem);
         System.out.println("✅ RSA key pair loaded from application.properties");
     } catch (Exception e) {
         throw new RuntimeException("❌ Failed to load RSA keys from application.properties", e);
     }
 }

 public String generateToken(String email, List<String> roles) {
     return Jwts.builder()
             .setSubject(email)
             .claim("roles", roles)
             .claim("authorities", roles)
             .setIssuedAt(new Date())
             .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
             .signWith(privateKey, SignatureAlgorithm.RS256)
             .compact();
 }

 public Claims extractClaims(String token) {
     return Jwts.parserBuilder()
             .setSigningKey(publicKey)
             .build()
             .parseClaimsJws(token)
             .getBody();
 }

 public boolean validateToken(String token) {
     try {
         extractClaims(token);
         return true;
     } catch (Exception e) {
         System.out.println("❌ Token validation failed: " + e.getMessage());
         return false;
     }
 }

 public RSAKey getRsaPublicKeyAsJwk() {
     return new RSAKey.Builder(publicKey)
             .keyID("auth-rsa-key")
             .build();
 }

 public RSAPublicKey getPublicKey() {
     return this.publicKey;
 }

 private RSAPublicKey getPublicKeyFromPem(String pem) throws Exception {
     String clean = pem.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                       .replaceAll("-----END PUBLIC KEY-----", "")
                       .replaceAll("\\s+", "");
     byte[] keyBytes = Base64.getDecoder().decode(clean);
     X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
     return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
 }

 private RSAPrivateKey getPrivateKeyFromPem(String pem) throws Exception {
     String clean = pem.replaceAll("-----BEGIN (RSA )?PRIVATE KEY-----", "")
                       .replaceAll("-----END (RSA )?PRIVATE KEY-----", "")
                       .replaceAll("\\s+", "");
     byte[] keyBytes = Base64.getDecoder().decode(clean);
     PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
     return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
 }
}
