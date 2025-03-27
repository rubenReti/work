package com.example.auth.controller;

import com.example.auth.model.Employee;
import com.example.shared.dto.AuthUserDTO;
import com.example.shared.dto.EmployeeDTO;
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




//cure for HR :curl -X POST "http://localhost:8082/auth/login?email=HR@example.com&password=HR121"
//curl -X POST "http://localhost:8082/auth/register" -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJIUkBleGFtcGxlLmNvbSIsInJvbGVzIjpbIkhSIl0sImF1dGhvcml0aWVzIjpbIkhSIl0sImlhdCI6MTc0MjgzODQzOSwiZXhwIjoyMDU4MTk4NDM5fQ.P8YBByyArz96j1pfK23rRMjDCB8eHtvaED6lSG-T7EVm0F8a46-PeFCBOWNw24U0U8UPdidm0z7e9CC6XxB0mazilLZzX3mqXpZmlLevq-oPpRNKxpeiw4l6Veglmr6fCMJwEB9oHDa20clmvf85qALof7xOCvy0H92UffDN10ZYMzuTs50HjAD1ebgk-9LtXk55VPOpV68MMgxtSIX-0a1fMHz_J-i0vVVeqPgCJV3NrDs4KlJpKpTfjbdv762xRyT6JXOmHeKO1RDIBewW70RXVHJjC4QKCxvx7JperrQnZwMfopcWAlRrvYMY1eh9q0lVGqspPyr2Vf4nPeG9dQ" -d "{\"email\":\"admin99@example.com\",\"password\":\"admin99\",\"role\":\"ADMIN\",\"username\":\"admin99\"}"
//User registered successfully!
//curl -X POST "http://localhost:8082/auth/login?email=user111@example.com&password=user111"



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

    
    @GetMapping("/ping-noauth")
    public String pingNoAuth() {
        System.out.println("✅ Ping-noauth hit!");
        return "pong";
    }

    
    @GetMapping("/ping")
    public String ping(Authentication authentication) {
        System.out.println("✅ Ping hit by: " + authentication.getName());
        return "pong";
    }

    
//    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody Employee employee, Authentication authentication) {
//        System.out.println("🔍 Register API hit!");
//        System.out.println("🔐 Authenticated as: " + authentication.getName());
//        System.out.println("🔐 Roles: " + authentication.getAuthorities());
//
//        System.out.println("🔍 Register API hit!");
//        System.out.println("📧 Email: " + employee.getEmail());
//        System.out.println("👤 Username: " + employee.getUsername());
//        System.out.println("🔐 Password: " + employee.getPassword());
//        System.out.println("🧩 Role: " + employee.getRole());
//        return ResponseEntity.ok(authService.register(employee));
//    }
    
    
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<String> register(@RequestBody EmployeeDTO dto) {
        authService.registerFromEmployeeDTO(dto);
        return ResponseEntity.ok("User registered successfully via public API");
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
