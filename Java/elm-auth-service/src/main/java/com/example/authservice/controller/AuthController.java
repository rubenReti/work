// ============================ AuthController.java ============================
package com.example.authservice.controller;

import com.example.authservice.security.JwtUtil;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return jwtUtil.generateToken(user.getEmail(), List.of("USER")); // Roles should be dynamic
    }
}
