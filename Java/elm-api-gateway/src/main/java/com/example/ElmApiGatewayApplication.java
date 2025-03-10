package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // <--- Add this annotation!
public class ElmApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElmApiGatewayApplication.class, args);
    }
}