package com.shintadev.shop_dev_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shintadev.shop_dev_be.security.jwt.AuthEntryPointJwt;
import com.shintadev.shop_dev_be.security.jwt.JwtAuthFilter;
import com.shintadev.shop_dev_be.security.oauth2.OAuth2AuthFailureHandler;
import com.shintadev.shop_dev_be.security.oauth2.OAuth2AuthSuccessHandler;
import com.shintadev.shop_dev_be.security.oauth2.OAuth2UserService;

import lombok.RequiredArgsConstructor;

/**
 * Security configuration for the application
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final AuthEntryPointJwt unauthorizedHandler;
  private final OAuth2UserService oAuth2UserService;
  private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
  private final OAuth2AuthFailureHandler oAuth2AuthFailureHandler;

  /**
   * Creates a new SecurityFilterChain bean
   * 
   * @param http the HttpSecurity object
   * @return the SecurityFilterChain bean
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/host").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/products/**").permitAll()
            .requestMatchers("/categories/**").permitAll()
            // Secured endpoints
            .requestMatchers("/users/**").hasRole("USER")
            .requestMatchers("/carts/**").hasRole("USER")
            .requestMatchers("/wishlists/**").hasRole("USER")
            .requestMatchers("/addresses/**").hasRole("USER")
            .requestMatchers("/orders/**").hasRole("USER")
            .requestMatchers("/payments/**").hasRole("USER")
            // Admin-only endpoints
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // All other endpoints must be authenticated
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
            .successHandler(oAuth2AuthSuccessHandler)
            .failureHandler(oAuth2AuthFailureHandler));

    return http.build();
  }

}
