package com.example.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UltimateLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("🧩 UltimateLoggingFilter INIT");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        System.out.println("📡 Request URI: " + req.getRequestURI());
        System.out.println("📡 Method: " + req.getMethod());
        System.out.println("📡 Auth Header: " + req.getHeader("Authorization"));

        chain.doFilter(request, response);  // continue filter chain
    }

    @Override
    public void destroy() {
        System.out.println("🧹 UltimateLoggingFilter DESTROY");
    }
}
