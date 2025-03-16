package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin123")
            .roles("ADMIN")
            .build();
        return new MapReactiveUserDetailsService(admin);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(MapReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/employees/**").hasAnyRole("ADMIN", "HR") // ✅ Allow HR & Admin
                .anyExchange().authenticated()
            )
            .httpBasic()
            .and()
            .formLogin();

        return http.build();
    }
}




//package com.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
//import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//            .username("admin")
//            .password("admin123")
//            .roles("ADMIN")
//            .build();
//        return new MapReactiveUserDetailsService(admin);
//    }
//
//    @Bean
//    public ReactiveAuthenticationManager authenticationManager(MapReactiveUserDetailsService userDetailsService) {
//        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http.csrf(csrf -> csrf.disable())  // 🔴 Disable CSRF for now
//            .authorizeExchange(exchange -> exchange
//                .pathMatchers("/actuator/**").permitAll()  // ✅ Allow all Actuator endpoints without authentication
//                .pathMatchers("/employees/**").hasRole("ADMIN")  // 🔐 Restrict access
//                .anyExchange().authenticated()
//            )
//            .httpBasic()
//            .and()
//            .formLogin();
//
//        return http.build();
//    }
//}
