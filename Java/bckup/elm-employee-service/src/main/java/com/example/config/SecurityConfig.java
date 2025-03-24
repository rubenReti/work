package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 🔴 Disable CSRF for now
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()  // ✅ Allow all Actuator endpoints publicly
                .requestMatchers("/employees/**").hasRole("ADMIN")  // 🔐 Restrict access
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }

    
    //in-memory user (admin/admin123) for testing
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin123")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
