package com.template;

import com.template.config.security.PasswordEncoder;
import com.template.user.entities.AuthorityEntity;
import com.template.user.entities.UserEntity;
import com.template.user.entities.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ApplicationBootstrap implements CommandLineRunner {

        private  final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {

            String str = passwordEncoder.hashPassword("admin");
            //System.out.println(str);
            // pre-populating the app with some demo data.
            //createUsers();
        }

        private void createUsers() {
            if(userRepository.count() == 0) {
                final UserEntity userEntity = new UserEntity();
                userEntity.setUsername("elka.ganeva@gmail.com");
                userEntity.setFirstName("Elka");
                userEntity.setLastName("Ganeva");
                userEntity.setPassword(passwordEncoder.hashPassword("admin"));

                AuthorityEntity adminRole = new AuthorityEntity().setRole("ROLE_ADMIN");
                AuthorityEntity userRole = new AuthorityEntity().setRole("ROLE_USER");
                userEntity.setRoles(List.of(adminRole, userRole));

                userRepository.save(userEntity);
            }
        }

}
