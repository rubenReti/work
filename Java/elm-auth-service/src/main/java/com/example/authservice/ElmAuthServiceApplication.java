// ============================ ElmAuthServiceApplication.java ============================
package com.example.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // Registers service with Eureka
public class ElmAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmAuthServiceApplication.class, args);
    }
}
