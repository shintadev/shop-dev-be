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

@Component
@Slf4j
@Getter
public class JwtTokenProvider {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-in-ms}")
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
