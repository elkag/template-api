package com.template.config.springfox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.template"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .securitySchemes(Collections.singletonList(apiKey()))
                .tags(
                        new Tag("Users controller", "Operations pertaining to users and user accounts"),
                        new Tag("Registration controller", "Operations pertaining to create user accounts"),
                        new Tag("Items controller (admin)", "Operations pertaining to products"),
                        new Tag("Items controller", "Operations pertaining to products"),
                        new Tag("Image controller", "Operations pertaining to upload images"),
                        new Tag("Statistics controller", "Operations pertaining to view information about users actions."));
    }

    private ApiKey apiKey() {
        return new ApiKey("jwtToken", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Template REST API",
                "\"Spring Boot REST API for Product list\"",
                "1.0",
                "Terms of service",
                new Contact("Elka Ganeva", "", "elka.ganeva@gmail.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0\"",
                Collections.emptyList());
    }

}
