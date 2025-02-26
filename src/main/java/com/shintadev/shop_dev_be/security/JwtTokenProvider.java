package com.shintadev.shop_dev_be.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class JwtTokenProvider {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.expiration-in-ms}")
  private Long expiration;

  private SecretKey secretKey;

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

  public String generateToken(UserDetails userDetails) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expiration))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean isTokenExpired(String token) {
    return getExpirationDateFromToken(token)
        .before(new Date());
  }

  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token)
        .getSubject();
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimsFromToken(token)
        .getExpiration();
  }

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
