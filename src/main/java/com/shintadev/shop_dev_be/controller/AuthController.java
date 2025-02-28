package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.auth.LoginRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.RegisterRequest;
import com.shintadev.shop_dev_be.security.jwt.JwtTokenProvider;
import com.shintadev.shop_dev_be.service.common.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return new ResponseEntity<>("Register successfully!", HttpStatus.CREATED);
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verify(@RequestParam String token) {
    authService.verify(token);
    return new ResponseEntity<>("Verify successfully!", HttpStatus.OK);
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<?> resendVerification(@RequestParam String email) {
    authService.resendVerification(email);
    return new ResponseEntity<>("Resend verification successfully!", HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    var token = authService.login(request);
    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    return new ResponseEntity<>("Login successfully!", headers, HttpStatus.OK);
  }

  // @PostMapping("/forgot-password")
  // public ResponseEntity<?> forgotPassword(@RequestParam String email) {
  // authService.forgotPassword(email);
  // return new ResponseEntity<>("Send reset password email successfully",
  // HttpStatus.OK);
  // }

  // @PostMapping("/reset-password")
  // public ResponseEntity<?> resetPassword(@RequestParam String token,
  // @RequestParam String email) {
  // authService.resetPassword(token, email);
  // return new ResponseEntity<>("Reset password successfully", HttpStatus.OK);
  // }

  // @PreAuthorize("isAuthenticated()")
  // @PostMapping("/change-password")
  // public ResponseEntity<?> changePassword(@RequestParam String token,
  // @RequestParam String newPassword) {
  // authService.changePassword(token, newPassword);
  // return new ResponseEntity<>("Change password successfully", HttpStatus.OK);
  // }

  // @PreAuthorize("isAuthenticated()")
  // @PostMapping("/logout")
  // public ResponseEntity<?> logout(@RequestParam String token) {
  // authService.logout(token);
  // return new ResponseEntity<>("Logout successfully", HttpStatus.OK);
  // }
}
