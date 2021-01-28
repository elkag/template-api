package com.template;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationBootstrap implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {

            //String str = passwordEncoder.hashPassword("admin");
            //System.out.println(str);
            // pre-populating the app with some demo data.
            //createUsers();
        }

}
