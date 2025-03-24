package com.example.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired  // ✅ Ensure Spring injects JwtUtil properly
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        
        // ✅ Allow login requests to pass without authentication
     // ✅ Allow JWKS requests to bypass authentication
        if (path.equals("/.well-known/jwks.json") || path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No JWT found in request.");
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.extractClaims(token);
            String email = claims.getSubject();
            List<String> rolesList = claims.get("roles", List.class);
            List<GrantedAuthority> authorities = rolesList.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            UserDetails userDetails = new User(email, "", authorities);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("✅ User Authenticated: " + email + " | Roles: " + authorities);

            // ✅ Check role for /auth/register
            if (path.equals("/auth/register")) {
                boolean isAuthorized = authorities.stream().anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_HR") || auth.getAuthority().equals("ROLE_MANAGER"));

                if (!isAuthorized) {
                    System.out.println("❌ Unauthorized access to /auth/register");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ JWT Authentication Error: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }

}
