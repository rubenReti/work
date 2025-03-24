package com.example.auth.controller;

import com.example.auth.model.Employee;
import com.example.auth.security.JwtUtil;
import com.example.auth.service.AuthService;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin // Optional, good to have
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        System.out.println("✅ AuthController Loaded!");
    }

    //@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Employee employee, Authentication authentication) {
        System.out.println("🔍 Register API hit!");
        System.out.println("🔐 Authenticated as: " + authentication.getName());
        System.out.println("🔐 Roles: " + authentication.getAuthorities());

        System.out.println("🔍 Register API hit!");
        System.out.println("📧 Email: " + employee.getEmail());
        System.out.println("👤 Username: " + employee.getUsername());
        System.out.println("🔐 Password: " + employee.getPassword());
        System.out.println("🧩 Role: " + employee.getRole());
        return ResponseEntity.ok(authService.register(employee));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        System.out.println("🔍 Login API hit with email: " + email);
        try {
            String token = authService.login(email, password);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        System.out.println("🔍 Validate API hit!");
        return ResponseEntity.ok(jwtUtil.validateToken(token));
    }

    // ✅ NEW — JWKS Endpoint for public key
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> exposeJwk() {
        System.out.println("🔑 Exposing JWKS...");
        return new JWKSet(jwtUtil.getRsaPublicKeyAsJwk()).toJSONObject();
    }
    
    
}
