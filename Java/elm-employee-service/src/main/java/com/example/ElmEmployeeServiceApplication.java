package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // Enables Eureka registration
public class ElmEmployeeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmEmployeeServiceApplication.class, args);
    }
}