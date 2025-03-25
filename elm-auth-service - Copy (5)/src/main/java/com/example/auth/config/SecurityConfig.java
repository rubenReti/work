package com.example.auth.config;

import com.example.auth.security.JwtUtil;
import jakarta.servlet.Filter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("🔐 SecurityFilterChain initializing...");

        http
            .addFilterBefore(loggingFilter(), AnonymousAuthenticationFilter.class)
            .addFilterAfter(jwtDebugFilter(), AnonymousAuthenticationFilter.class)
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/login",
                    "/auth/test",
                    "/.well-known/jwks.json",
                    "/auth/ping-noauth"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        System.out.println("🔧 JwtAuthenticationConverter initialized with 'roles' → ROLE_ mapping");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles != null) {
                for (String role : roles) {
                    System.out.println("🔑 Mapped JWT role: " + role + " → ROLE_" + role);
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            } else {
                System.out.println("⚠️ No 'roles' claim found in token.");
            }

            return authorities;
        });
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        System.out.println("🔐 Loading RSA Public Key from memory instead of JWKS");
        return NimbusJwtDecoder.withPublicKey(jwtUtil.getPublicKey()).build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Filter loggingFilter() {
        return (request, response, chain) -> {
            String path = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
            System.out.println("📡 Filter hit: " + path);
            chain.doFilter(request, response);
        };
    }

    @Bean
    public Filter jwtDebugFilter() {
        return (request, response, chain) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                System.out.println("🟢 Authenticated as: " + auth.getName());
                System.out.println("🟢 Authorities: " + auth.getAuthorities());
            } else {
                System.out.println("🔴 No authentication present.");
            }
            chain.doFilter(request, response);
        };
    }
}
