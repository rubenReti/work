package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import reactor.core.publisher.Mono;


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

    
    
//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:8082/.well-known/jwks.json").build();
//    }

    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/**").permitAll()  // ✅ Open Actuator
                .pathMatchers("/auth/login").permitAll()  // ✅ Allow login without authentication
                .pathMatchers("/auth/.well-known/jwks.json").permitAll() // ✅ Add this
                .pathMatchers("/auth/register").hasAnyRole("HR", "ADMIN")  // 🔐 Only HR/Admin can register
                .pathMatchers("/employees/**").hasAnyRole("ADMIN", "HR")  // 🔐 Employees require proper role
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> 
            oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
        )            .httpBasic()
            .and()
            .formLogin();

        return http.build();
    }
    
    private Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthoritiesClaimName("roles");
        delegate.setAuthorityPrefix("ROLE_");

        return jwt -> {
            Collection<GrantedAuthority> authorities = delegate.convert(jwt);
            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//            .authorizeExchange(exchange -> exchange
//                .pathMatchers("/actuator/**").permitAll()  // ✅ Open Actuator
//                .pathMatchers("/auth/login").permitAll()  // ✅ Allow login without authentication
//                .pathMatchers("/auth/register").authenticated()  // 🔐 Require authentication for registration
//                .pathMatchers("/employees/**").hasAnyRole("ADMIN", "HR")  // 🔐 Employees require Basic Auth
//                .anyExchange().authenticated()
//            )
//            .httpBasic()
//            .and()
//            .formLogin();
//
//        return http.build();
//    }

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//            .authorizeExchange(exchange -> exchange
//                .pathMatchers("/actuator/**").permitAll()
//                .pathMatchers("/employees/**").hasAnyRole("ADMIN", "HR") // ✅ Allow HR & Admin
//                .anyExchange().authenticated()
//            )
//            .httpBasic()
//            .and()
//            .formLogin();
//
//        return http.build();
//    }
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
