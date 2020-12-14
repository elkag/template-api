package com.template.config.security;

import com.template.logger.rest.interceptors.StatsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final StatsInterceptor statsInterceptor;

  public WebConfiguration(StatsInterceptor statsInterceptor) {
    this.statsInterceptor = statsInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(statsInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    final long MAX_AGE_SECS = 3600;

    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(MAX_AGE_SECS);
  }
}