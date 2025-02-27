package com.shintadev.shop_dev_be.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Extract the token from the request
      final String token = jwtTokenProvider.resolveToken(request);
      log.info("Token: {}", token);
      if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        // Extract the username from the token
        final String username = jwtTokenProvider.getUsernameFromToken(token);
        log.info("Username: {}", username);
        // Load the user details from the database
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.info("UserDetails: {}", userDetails.toString());
        // Create an authentication token
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        // Set the details of the authentication token
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Set the authentication object in the security context
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception e) {
      log.error("Error while processing authentication filter", e);
    }
    filterChain.doFilter(request, response);
  }
}
