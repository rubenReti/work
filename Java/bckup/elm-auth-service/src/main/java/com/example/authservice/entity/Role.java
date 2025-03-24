package com.example.authservice.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    HR,
    MANAGER,
    USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
