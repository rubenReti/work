package com.example.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, String>>> authFallback() {
        log.warn("🔥 Gateway fallback triggered for Auth service");
        System.out.println("🔥 Gateway fallback triggered for Auth servi!!!");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", "AUTH_SERVICE_UNAVAILABLE",
                         "message", "Auth Service is currently unavailable. Please try again later.")));
    }

    @GetMapping("/employees")
    public Mono<ResponseEntity<Map<String, String>>> employeeFallback() {
        log.warn("🔥 Gateway fallback triggered for Employee service");

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", "EMPLOYEE_SERVICE_UNAVAILABLE",
                         "message", "Employee Service is temporarily down. Please try again later.")));
    }
}
