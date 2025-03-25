package com.example.auth.service;

import com.example.auth.model.Employee;
import com.example.auth.model.Role;
import com.example.auth.repository.EmployeeRepository;
import com.example.auth.security.JwtUtil;
import com.example.shared.dto.AuthUserDTO;

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

    
    
    public String register(AuthUserDTO dto) {
        System.out.println("📥 Registering employee with DTO: " + dto.getEmail());

        Employee employee = new Employee();
        employee.setUsername(dto.getUsername());
        employee.setEmail(dto.getEmail());
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        employee.setRole(Role.valueOf(dto.getRole())); // Convert string to enum

        try {
            employeeRepository.save(employee);
            System.out.println("✅ Employee saved.");
        } catch (Exception e) {
            System.err.println("❌ DB Save Error: " + e.getMessage());
            throw e;
        }

        return "User registered successfully!";
    }

//    public String register(Employee employee) {
//        System.out.println("📥 Registering employee:");
//        System.out.println("📧 Email: " + employee.getEmail());
//        System.out.println("👤 Username: " + employee.getUsername());
//        System.out.println("🔐 Raw Password: " + employee.getPassword());
//        System.out.println("🧩 Role: " + employee.getRole());
//
//        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
//
//        try {
//            employeeRepository.save(employee);
//            System.out.println("✅ Employee saved.");
//        } catch (Exception e) {
//            System.err.println("❌ DB Save Error: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        }
//
//        return "User registered successfully!";
//    }


    
    
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
