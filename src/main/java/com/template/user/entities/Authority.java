package com.template.user.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    USER,
    AUTHOR,
    ADMIN,
    SUPER_ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
