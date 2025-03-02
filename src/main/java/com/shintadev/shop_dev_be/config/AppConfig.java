package com.shintadev.shop_dev_be.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.slugify.Slugify;

import lombok.RequiredArgsConstructor;

/**
 * Application configuration
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

  private final UserDetailsService userDetailsService;

  /**
   * Creates a new BCryptPasswordEncoder bean
   * 
   * @return the BCryptPasswordEncoder bean
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder() {

    // Uncomment this block to use plain text password
      @Override
      public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
      }
    };
  }

  /**
   * Creates a new AuthenticationProvider bean
   * 
   * @return the AuthenticationProvider bean
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Creates a new AuthenticationManager bean
   * 
   * @param config the AuthenticationConfiguration
   * @return the AuthenticationManager bean
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Creates a new Slugify bean
   * 
   * @return the Slugify bean
   */
  @Bean
  public Slugify slugify() {
    return Slugify.builder()
        .locale(Locale.getDefault())
        .customReplacement("_", "-")
        .build();
  }
}
