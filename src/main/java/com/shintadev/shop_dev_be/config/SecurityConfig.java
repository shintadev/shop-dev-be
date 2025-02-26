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

import com.shintadev.shop_dev_be.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/products/**").permitAll()
            // Secured endpoints
            .requestMatchers("/api/users/**").authenticated()
            .requestMatchers("/api/orders/**").authenticated()
            .requestMatchers("/api/payments/**").authenticated()
            .requestMatchers("/api/reviews/**").authenticated()
            .requestMatchers("/api/carts/**").authenticated()
            .requestMatchers("/api/addresses/**").authenticated()
            .requestMatchers("/api/wishlists/**").authenticated()
            // Admin-only endpoints
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            // All other endpoints must be authenticated
            .anyRequest().authenticated());
    return http.build();
  }

}
