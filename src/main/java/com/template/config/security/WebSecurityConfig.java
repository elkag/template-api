package com.template.config.security;

import com.template.user.RestAuthenticationEntryPoint;
import com.template.user.service.impl.UserDetailsServiceImpl;
import com.template.config.security.jwt.JWTAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.template.config.security.SecurityConstants.USERS_LOGIN_URL;
import static com.template.config.security.SecurityConstants.USERS_REGISTER_URL;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final TokenProvider tokenProvider;
  private final UserDetailsServiceImpl userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public JWTAuthorizationFilter tokenAuthenticationFilter() {
    return new JWTAuthorizationFilter(tokenProvider, userDetailsService);
  }

  @Autowired
  protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .cors()
        .and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
            .exceptionHandling()
              .authenticationEntryPoint(new RestAuthenticationEntryPoint())
              .and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, USERS_LOGIN_URL, USERS_REGISTER_URL)
              .permitAll()
            .antMatchers(HttpMethod.GET)
              .permitAll()
            .antMatchers("/auth/**")
              .permitAll()
            .anyRequest().authenticated();

    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(final WebSecurity web) {
    web.ignoring().antMatchers(
        "/users/**",
        "/configuration/ui",
        "/configuration/**",
        "/actuator/**",
        "/v2/api-docs",
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/webjars/**");
  }
}
