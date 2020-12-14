package com.template.config;

import com.template.logger.rest.interceptors.UserActionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorsConfig implements WebMvcConfigurer {

    private final UserActionInterceptor userActionInterceptor;

    public InterceptorsConfig(UserActionInterceptor userActionInterceptor) {
        this.userActionInterceptor = userActionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userActionInterceptor).addPathPatterns("/items/get/", "/items/search");
    }
}
