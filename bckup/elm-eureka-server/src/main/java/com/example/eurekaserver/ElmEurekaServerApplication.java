package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer  // Enables Eureka Server functionality
public class ElmEurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmEurekaServerApplication.class, args);
    }
}
