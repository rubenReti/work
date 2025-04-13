package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example",
    "com.example.controller",
    "com.example.config",
    "com.example.service",
    "com.example.kafka"  // optional if you want to include Kafka listeners explicitly
})
public class ElmLeaveServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmLeaveServiceApplication.class, args);
        System.out.println("🌿 Leave Service is up.");
    }
}
