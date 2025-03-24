//
//package com.example.auth.controller;
//
//import com.example.auth.model.Employee;
//import com.example.auth.security.JwtUtil;
//import com.example.auth.service.AuthService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//    static {
//        System.out.println("🔍 AuthController Loaded!");
//    }
//
//    @GetMapping("/test")
//    public String testController() {
//        return "AuthController is working!";
//    }
//}
//
//








package com.example.auth.controller;

import com.example.auth.model.Employee;
import com.example.auth.security.JwtUtil;
import com.example.auth.service.AuthService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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

//    //regoster for no autentication case
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody Employee employee) {
//        System.out.println("🔍 Register API hit!");
//        return ResponseEntity.ok(authService.register(employee));
//    }
  
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Employee employee, Authentication authentication) {
        System.out.println("🔍 Register API hit!");
        
        if (authentication == null) {
            System.out.println("❌ Authentication is NULL! Spring Security is not passing the user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed!");
        }

        System.out.println("🔍 User Authenticated: " + authentication.getName());
        System.out.println("🔍 User Roles: " + authentication.getAuthorities()); // ✅ Debugging

        return ResponseEntity.ok(authService.register(employee));
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        System.out.println("🔍 Login API hit with email: " + email); // Debugging
        try {
            String token = authService.login(email, password);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {

            e.printStackTrace();  // ✅ Ensure errors are printed in logs
            System.out.println("❌ Login failed: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    
    
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
//        System.out.println("🔍 Login API hit!");
//        return ResponseEntity.ok(authService.login(email, password));
//    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        System.out.println("🔍 Validate API hit!");
        return ResponseEntity.ok(jwtUtil.validateToken(token));
    }
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody Employee employee) {
//        return ResponseEntity.ok(authService.register(employee));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
//        return ResponseEntity.ok(authService.login(email, password));
//    }
//
//    @GetMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestParam String token) {
//        return ResponseEntity.ok(jwtUtil.validateToken(token));
//    }
//
//    @GetMapping("/roles")
//    public ResponseEntity<List<String>> getRoles(@RequestParam String token) {
//        return ResponseEntity.ok(authService.getRolesFromToken(token));
//    }
}
