package com.template.user.entities;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    SUPER_ADMIN,
    ADMIN,
    AUTHOR,
    USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
