  package com.example.auth.service;

import com.example.auth.kafka.AuthEventProducer;
import com.example.auth.model.Employee;
import com.example.auth.model.Role;
import com.example.auth.repository.EmployeeRepository;
import com.example.auth.security.JwtUtil;
import com.example.shared.dto.AuthUserDTO;
import com.example.shared.dto.EmployeeDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
	
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	
	String defaultPswd = "defaultPassword123";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AuthEventProducer eventProducer; // Add this field
    
    public void registerFromEmployeeDTO(EmployeeDTO dto) {
        String username = (dto.getFirstName() + "." + dto.getLastName()).toLowerCase();
        String role = mapDepartmentToRole(dto.getDepartment());

        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setEmail(dto.getEmail());
        employee.setPassword(passwordEncoder.encode(defaultPswd));
        employee.setRole(Role.valueOf(role));

        try {
            employeeRepository.save(employee);

            log.info("✅ Auth user registered from EmployeeDTO: {}", dto.getEmail());
            
            

            // Notify Notification Service
            AuthUserDTO userDTO = new AuthUserDTO(
                null,
                username,
                dto.getEmail(),
                defaultPswd, // only here for Notification
                role
            );
            eventProducer.sendAuthUserCreated(userDTO);
            

        } catch (Exception e) {
            log.error("❌ DB Save Error during registration", e);
            throw e;
        }
    }


    
    public String login(String email, String password) {
    	log.info("✅ Auth user LOGIN -  mail: {}", email);


        Optional<Employee> user = employeeRepository.findByEmail(email);
        if (user.isEmpty()) {
        	log.error("❌ User not found: " + email);
            throw new RuntimeException("Invalid credentials");
        }

        Employee employee = user.get();
        log.info("🔍 Stored password (hashed): " + employee.getPassword());
        log.info("🔍 Entered password: " + password);

        if (!passwordEncoder.matches(password, employee.getPassword())) {
        	log.error("❌ Password mismatch for: " + email);
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(email, Collections.singletonList(employee.getRole().name()));
    }

    


    public List<String> getRolesFromToken(String token) {
        return jwtUtil.extractClaims(token).get("roles", List.class);
    }
    
    
    
    
    //called from kafka's listener 
    public void updateAuthUserFromEmployeeDTO(EmployeeDTO dto) {
        String email = dto.getEmail();
        Optional<Employee> optional = employeeRepository.findByEmail(email);

        if (optional.isEmpty()) {
        	log.error("❌ No auth user found for email: " + email);
            return;
        }

        Employee existing = optional.get();

        String username = (dto.getFirstName() + "." + dto.getLastName()).toLowerCase();
        String role = mapDepartmentToRole(dto.getDepartment());

        existing.setUsername(username);
        existing.setEmail(email);
        existing.setRole(Role.valueOf(role));

        employeeRepository.save(existing);
        log.info("✅ Auth user updated for email: " + email);
    }
    
    
    
    
    
    
    public void deleteUserByEmail(String email) {
        Optional<Employee> optional = employeeRepository.findByEmail(email);
        optional.ifPresent(employee -> {
            employeeRepository.delete(employee);
            log.info("🗑 Auth user deleted: " + email);
        });
    }


    
    //privte stuffs

    private String mapDepartmentToRole(String department) {
        return switch (department.toLowerCase()) {
            case "hr" -> "HR";
            case "it" -> "ADMIN";
            case "management" -> "MANAGER";
            default -> "USER";
        };
    }

}
