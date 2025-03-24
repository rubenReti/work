package com.example.auth.service;

import com.example.auth.model.Employee;
import com.example.auth.repository.EmployeeRepository;
import com.example.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(Employee employee) {
        // Hash the password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
        return "User registered successfully!";
    }

    
    
    public String login(String email, String password) {
        System.out.println("🔍 Attempting login for: " + email);

        Optional<Employee> user = employeeRepository.findByEmail(email);
        if (user.isEmpty()) {
            System.out.println("❌ User not found: " + email);
            throw new RuntimeException("Invalid credentials");
        }

        Employee employee = user.get();
        System.out.println("🔍 Stored password (hashed): " + employee.getPassword());
        System.out.println("🔍 Entered password: " + password);

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            System.out.println("❌ Password mismatch for: " + email);
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(email, Collections.singletonList(employee.getRole().name()));
    }

    
    
//    public String login(String email, String password) {
//        System.out.println("🔍 Attempting login for email: " + email); // Debug log
//
//        Optional<Employee> user = employeeRepository.findByEmail(email);
//        if (user.isEmpty()) {
//            System.out.println("❌ User not found: " + email);
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        Employee employee = user.get();
//
//        if (!passwordEncoder.matches(password, employee.getPassword())) {
//            System.out.println("❌ Incorrect password for email: " + email);
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        // Generate JWT token with user role
//        System.out.println("✅ Login successful for email: " + email);
//        return jwtUtil.generateToken(email, Collections.singletonList(employee.getRole().name()));
//    }

    public List<String> getRolesFromToken(String token) {
        return jwtUtil.extractClaims(token).get("roles", List.class);
    }
}
