package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "com.example.auth") // Ensure it scans all packages
//@SpringBootApplication(scanBasePackages = {"com.example.auth.controller"})
@SpringBootApplication(scanBasePackages = {"com.example.auth", "com.example.auth.controller"})
public class ElmAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElmAuthServiceApplication.class, args);
       

//public class ElmAuthServiceApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(ElmAuthServiceApplication.class, args);
    }
}
