package com.template.user.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="roles")
public class AuthorityEntity implements GrantedAuthority {

    public AuthorityEntity(String role) {
        this.role = role;
    }

    public AuthorityEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    public AuthorityEntity setRole(String role) {
        this.role = role;
        return this;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
