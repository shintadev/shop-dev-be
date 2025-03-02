package com.shintadev.shop_dev_be.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides JWT token generation and validation
 */
@Component
@Slf4j
@Getter
public class JwtTokenProvider {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-in-ms}")
  private Long expiration;

  private SecretKey secretKey;

  /**
   * Initializes the JwtTokenProvider
   */
  @PostConstruct
  public void init() {
    try {
      log.info("Initializing JwtTokenProvider with secret: {}", secret);
      secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("Error initializing JwtTokenProvider", e);
      throw e;
    }
  }

  /**
   * Generates a JWT token for a user
   * 
   * @param userDetails the user details
   * @return the JWT token
   */
  public String generateToken(UserDetails userDetails) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expiration))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Resolves the JWT token from the request
   * 
   * @param request the HTTP request
   * @return the JWT token
   */
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * Validates a JWT token
   * 
   * @param token the JWT token
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      getClaimsFromToken(token);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Checks if a JWT token is expired, throws an exception if it is. Recommended
   * to
   * use {@link #validateToken(String)} instead or handle the exception yourself.
   * 
   * @param token the JWT token
   * @return true if the token is expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    return getExpirationDateFromToken(token)
        .before(new Date());
  }

  /**
   * Gets the username from a JWT token, throws an exception if the token is
   * invalid. Recommended to use {@link #validateToken(String)} instead or
   * handle the exception yourself.
   * 
   * @param token the JWT token
   * @return the username
   */
  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token)
        .getSubject();
  }

  /**
   * Gets the expiration date from a JWT token, throws an exception if the token
   * is invalid. Recommended to use {@link #validateToken(String)} instead or
   * handle the exception yourself.
   * 
   * @param token the JWT token
   * @return the expiration date
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimsFromToken(token)
        .getExpiration();
  }

  /**
   * Gets the claims from a JWT token, throws an exception if the token is
   * invalid. Recommended to use {@link #validateToken(String)} instead or
   * handle the exception yourself.
   * 
   * @param token the JWT token
   * @return the claims
   */
  public Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (JwtException e) {
      log.error("Error getting claims from token: {}", e.getMessage());
      throw e;
    }
  }
}
