package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.auth.ChangePasswordRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.LoginRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.RegisterRequest;
import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.service.common.AuthService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

/**
 * AuthController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-03
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthService authService;

  /**
   * Register a new user
   * 
   * @param request the register request
   * @return the response entity
   */
  @PostMapping("/register")
  public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse(true, "Register successfully!", null));
  }

  /**
   * Verify a user by token sent to their email
   * 
   * @param token the token
   * @return the response entity
   */
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse> verify(@RequestParam String token) {
    authService.verify(token);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Verify successfully!", null));
  }

  /**
   * Resend verification email
   * 
   * @param email the email
   * @return the response entity
   */
  @PostMapping("/resend-verification")
  public ResponseEntity<ApiResponse> resendVerification(@RequestParam String email) {
    authService.resendVerification(email);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Resend verification successfully!", null));
  }

  /**
   * Login a user
   * 
   * @param request the login request
   * @return the response entity
   */
  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
    var token = authService.login(request);
    var headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(new ApiResponse(true, "Login successfully!", null));
  }

  /**
   * Send reset password email
   * 
   * @param email the email
   * @return the response entity
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
    authService.forgotPassword(email);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Send reset password email successfully", null));
  }

  /**
   * Reset password
   * 
   * @param token       the token
   * @param newPassword the new password
   * @return the response entity
   */
  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse> resetPassword(
      @NotBlank(message = "Token is required") @RequestParam String token,
      @NotBlank(message = "New password is required") @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character") @RequestParam String newPassword) {
    authService.resetPassword(token, newPassword);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Reset password successfully", null));
  }

  /**
   * Change password
   * 
   * @param request the change password request
   * @return the response entity
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Change password successfully", null));
  }

}
