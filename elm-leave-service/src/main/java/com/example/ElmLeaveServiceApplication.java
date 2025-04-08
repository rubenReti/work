package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElmLeaveServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmLeaveServiceApplication.class, args);
        System.out.println("🌿 Leave Service is up.");
    }
}
