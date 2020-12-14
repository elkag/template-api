package com.template.config;

import com.template.config.security.PasswordEncoder;
import com.template.user.entities.*;
import com.template.user.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@TestConfiguration
public class SpringSecurityTestConfig {
    
    public static UserEntity SUPER_ADMIN;
    public static UserEntity ADMIN;
    public static UserEntity AUTHOR;

    @Autowired
    UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        SUPER_ADMIN = new UserEntity();
        SUPER_ADMIN.setUsername("superadmin");
        SUPER_ADMIN.setPassword(PasswordEncoder.hashPassword("superadmin"));
        SUPER_ADMIN.setFirstName("superadmin");
        SUPER_ADMIN.setLastName("superadmin");

        SUPER_ADMIN.setRoles(List.of(
                new AuthorityEntity(Authority.SUPER_ADMIN.name()),
                new AuthorityEntity(Authority.ADMIN.name()),
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name())));
        userRepository.save(SUPER_ADMIN);

        ADMIN = new UserEntity();
        ADMIN.setUsername("admin");
        ADMIN.setPassword(PasswordEncoder.hashPassword("admin"));
        ADMIN.setFirstName("admin");
        ADMIN.setLastName("admin");

        ADMIN.setRoles(List.of(
                new AuthorityEntity(Authority.ADMIN.name()),
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name())));
        userRepository.save(ADMIN);

        AUTHOR = new UserEntity();
        AUTHOR.setUsername("author");
        AUTHOR.setPassword(PasswordEncoder.hashPassword("author"));
        AUTHOR.setFirstName("author");
        AUTHOR.setLastName("author");

        AUTHOR.setRoles(List.of(
                new AuthorityEntity(Authority.AUTHOR.name()),
                new AuthorityEntity(Authority.USER.name())));
        userRepository.save(AUTHOR);

        return new UserDetailsServiceImpl(userRepository);
    }
}
