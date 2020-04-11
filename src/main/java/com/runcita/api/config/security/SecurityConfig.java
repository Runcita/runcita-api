package com.runcita.api.config.security;

import com.runcita.api.config.security.jwt.JWTConfigurer;
import com.runcita.api.config.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Spring security config for dev, preprod and prod environment
 */
@Profile({"dev", "preprod", "prod"})
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final TokenProvider tokenProvider;

  public SecurityConfig(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .headers()
      .frameOptions()
      .disable()
      .and()
      .csrf()
      .disable()
      .cors()
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/signin").permitAll()
      .antMatchers("/signup").permitAll()
      .antMatchers("/public").permitAll()
      .antMatchers("/ping").permitAll()
      .antMatchers("/v3/api-docs").permitAll()
      .antMatchers("/swagger-ui/**").permitAll()
      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .anyRequest().authenticated()
      .and()
      .apply(new JWTConfigurer(this.tokenProvider));
  }
}
