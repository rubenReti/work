package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//curl -X POST "http://localhost:8080/auth/login?email=HR@example.com&password=HR121"

//C:\Windows\System32>curl -X POST "http://localhost:8080/auth/register" -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJIUkBleGFtcGxlLmNvbSIsInJvbGVzIjpbIkhSIl0sImF1dGhvcml0aWVzIjpbIkhSIl0sImlhdCI6MTc0Mjg0NzkyNCwiZXhwIjoyMDU4MjA3OTI0fQ.EJ5kFI9CumoI7ho36KExcxl7FJ_9HgUl2RvPofxJ6YLgHYtJKK8DWgRb18zhNKOvP4DA93a0oiEEh0MpgAoGKDlrOECVr1BBFB65sWF_68D0X47uxxlEkf5xuLDMfvf1JAImK8Re9cN3izdWclY0fUxcMOJDxf9c3dThSWHxcwTZjvtAE_CploOpRO3eV9DORfyYv4ExAKZMoGWl2VZ4xUp59yKjPVLLLTsO2VBx8a-UXt60z_KHvIMf3dzivRmPYqp3COu-t_e7uO8jsrR1ZYmyTSXJS3CJZjKJgAogi_IQEgmvcn39TEavotcw9eE0PmWQ5yLs0tO2AgcQjfyNnw" -d "{\"email\":\"admin99@example.com\",\"password\":\"admin99\",\"role\":\"ADMIN\",\"username\":\"admin99\"}"
//User registered successfully!


@SpringBootApplication(scanBasePackages = "com.example")
@EnableDiscoveryClient  // <--- Add this annotation!
public class ElmApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElmApiGatewayApplication.class, args);
    }
}