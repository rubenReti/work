package com.example.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/employees")
    public ResponseEntity<Map<String, String>> employeeServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "EMPLOYEE_SERVICE_UNAVAILABLE",
                        "message", "Employee Service is temporarily down. Please try again later."
                ));
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "AUTH_SERVICE_UNAVAILABLE",
                        "message", "Auth Service is currently unavailable. Please try again later."
                ));
    }
}
