package com.template.user.service.impl;

import com.template.user.entities.UserEntity;
import com.template.user.entities.UserPrincipal;
import com.template.user.entities.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<UserEntity> userOpt = userRepository
                .findOneByUsername(username);

        if(userOpt.isEmpty()){
            throw  new UsernameNotFoundException("User entity does not exist: " + username);
        }
        return new UserPrincipal(userOpt.get());
    }
}
