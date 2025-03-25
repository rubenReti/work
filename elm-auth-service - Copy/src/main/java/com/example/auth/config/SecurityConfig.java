package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }	
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/validate").permitAll()  // ✅ Public APIs
                .requestMatchers("/auth/register").hasAnyRole("ADMIN", "HR")  // 🔒 Only ADMIN or HR can register
                .requestMatchers("/auth/roles").authenticated()  // 🔒 Only authenticated users can fetch roles
                .anyRequest().authenticated()  // 🔒 Everything else requires authentication
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);  // ✅ Enforce JWT authentication

        return http.build();
    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()  // ✅ Disable CSRF to allow requests
//            .cors().disable()  // ✅ Disable CORS (for debugging)
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/**").permitAll()  // ✅ Explicitly allow /auth/**
//                .anyRequest().permitAll()  // ✅ Temporarily allow everything
//            )
//            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
    
    
    //OLD TTRIES
//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    http
//	        .csrf().disable()
//	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers("/auth/register", "/auth/login", "/auth/test").permitAll() // Allow only these
//	            .anyRequest().authenticated()
//	        )
//	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
//	    return http.build();
//	}

//permit all!~!!!!	
   

     


	
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/register", "/auth/login", "/auth/validate").permitAll()
//                .anyRequest().authenticated()
//            )
//            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        return http.build();
//    }


//	no secure:
//	@Bean
//	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    http.csrf().disable()
//	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers("/auth/**").permitAll() // Allow all requests to /auth
//	            .anyRequest().authenticated()
//	        )
//	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//	    return http.build();
//	}
//ORIG:
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/login", "/auth/register").permitAll()
//                .anyRequest().authenticated()
//            )
//            .httpBasic();
//        return http.build();
//    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
