package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElmNotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmNotificationServiceApplication.class, args);
        System.out.println("📬 Notification Service Started.");
    }
}
