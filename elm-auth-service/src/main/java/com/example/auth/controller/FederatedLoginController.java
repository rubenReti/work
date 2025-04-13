package com.example.auth.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.model.Employee;
import com.example.auth.repository.EmployeeRepository;
import com.example.auth.security.GoogleTokenVerifier;
import com.example.auth.security.JwtUtil;
import com.example.auth.security.federated.FederatedIdentityResolver;



@RestController
@RequestMapping("/auth/federated")
public class FederatedLoginController {

    @Autowired
    private FederatedIdentityResolver resolver;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeRepository employeeRepository;

    
    //provider -> POST /auth/federated/login/google OR POST /auth/federated/login/microsoft OR ...

    @PostMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider, @RequestBody String idToken) {
        try {
            var identityProvider = resolver.get(provider);  //get it for google
            if (identityProvider == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Unsupported provider: " + provider));
            }

            String email = identityProvider.extractEmail(idToken);
            var user = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No employee found for: " + email));

            String token = jwtUtil.generateToken(email, List.of(user.getRole().name()));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }
}



//@RestController
//@RequestMapping("/auth/google")
//public class GoogleAuthController {
//	
//	private static final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);
//
//	
//	  @Autowired
//	    private GoogleTokenVerifier googleVerifier;
//
//	    @Autowired
//	    private EmployeeRepository employeeRepository;
//
//	    @Autowired
//	    private JwtUtil jwtUtil;
//
//	    @PostMapping("/login")
//	    public ResponseEntity<?> login(@RequestBody String idToken) {
//	    	
//	    	  try {
//	    	        String email = googleVerifier.extractEmailFromToken(idToken);
//	    	        log.info("✅ Google email: " + email);
//
//
//	    	        Optional<Employee> optional = employeeRepository.findByEmail(email);
//	    	        if (optional.isEmpty()) {
//	    	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	    	                .body(Map.of("error", "No user found for email: " + email));
//	    	        }
//
//	    	        Employee user = optional.get();
//	    	        String token = jwtUtil.generateToken(email, List.of(user.getRole().name()));
//
//	    	        return ResponseEntity.ok(Map.of("token", token));
//	    	    } catch (Exception e) {
//	    	      
//	    	        log.error("❌ Google token validation failed: " + e.getMessage());
//	    	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	    	            .body(Map.of("error", "Invalid Google token"));
//	    	    }
//	        // same logic
//	    }
//
//}
