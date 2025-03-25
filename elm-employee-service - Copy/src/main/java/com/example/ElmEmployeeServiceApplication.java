package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example", 
    "com.example.controller", 
    "com.example.config", 
    "com.example.service"
})
public class ElmEmployeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElmEmployeeServiceApplication.class, args);
    }

    @PostConstruct
    public void startupLog() {
        System.out.println("🚀 Employee Service Booted. Logging is ACTIVE.");
    }
}
