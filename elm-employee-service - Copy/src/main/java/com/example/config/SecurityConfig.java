package com.example.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize annotations
public class SecurityConfig {

    
	@Bean
	public FilterRegistrationBean<UltimateLoggingFilter> ultimateLoggingFilterRegistration(UltimateLoggingFilter filter) {
	    FilterRegistrationBean<UltimateLoggingFilter> registration = new FilterRegistrationBean<>();
	    registration.setFilter(filter);
	    registration.addUrlPatterns("/*"); // intercept all URLs
	    registration.setOrder(1); // make sure it's early in the chain
	    return registration;
	}

	
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/test-open").permitAll() 
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/employees/**").hasAnyRole("ADMIN", "HR")
                .anyRequest().authenticated()
            )
//            .oauth2ResourceServer(oauth -> oauth
//                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
//            )
            ;
        
        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomRoleConverter());
        return converter;
    }

    static class CustomRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return new ArrayList<>();
            }

            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
    
    @Bean
    public FilterRegistrationBean<UltimateLoggingFilter> loggingFilterBean() {
        FilterRegistrationBean<UltimateLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UltimateLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<UltimateLoggingFilter> ultimateLoggingFilterBean() {
        FilterRegistrationBean<UltimateLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UltimateLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1); // Run early
        return registrationBean;
    }

}
