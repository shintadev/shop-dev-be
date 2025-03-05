package com.shintadev.shop_dev_be.service.common.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shintadev.shop_dev_be.constant.KafkaConstants;
import com.shintadev.shop_dev_be.domain.dto.request.auth.ChangePasswordRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.LoginRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.RegisterRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.EmailVerificationToken;
import com.shintadev.shop_dev_be.domain.model.entity.user.ResetPasswordToken;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.kafka.producer.EmailProducer;
import com.shintadev.shop_dev_be.repository.user.EmailVerificationTokenRepo;
import com.shintadev.shop_dev_be.repository.user.ResetPasswordTokenRepo;
import com.shintadev.shop_dev_be.security.jwt.JwtTokenProvider;
import com.shintadev.shop_dev_be.service.common.AuthService;
import com.shintadev.shop_dev_be.service.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the AuthService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final EmailVerificationTokenRepo emailVerificationTokenRepo;
  private final ResetPasswordTokenRepo resetPasswordTokenRepo;
  private final PasswordEncoder passwordEncoder;
  private final EmailProducer emailProducer;

  /**
   * Registers a new user
   * 
   * @param request the register request DTO
   */
  @Override
  public void register(RegisterRequest request) {
    // 1. Create user
    UserResponse user = userService.createUser(request);

    // 2. Create email verification token
    EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
        .userId(user.getId())
        .expiryDate(LocalDateTime.now().plusDays(1))
        .build();
    log.info("Email verification token: {}", emailVerificationToken);
    emailVerificationTokenRepo.save(emailVerificationToken);

    // 3. Create email data
    Map<String, Object> emailData = createEmailData(user, "Verify your email");

    // 4. Add verification link to email data
    emailData.put(KafkaConstants.VERIFICATION_LINK_KEY,
        "http://localhost:8080/api/auth/verify?token=" + emailVerificationToken.getId().toString());

    // 5. Send verification email
    emailProducer.sendVerificationEmail(emailData);
    log.info("Verification email sent to {}", user.getEmail());
    log.info("Link: {}", "http://localhost:8080/api/auth/verify?token=" + emailVerificationToken.getId().toString());
  }

  /**
   * Verifies a user's email
   * 
   * @param token the verification token
   */
  @Override
  public void verify(String token) {
    // 1. Check if token is exists
    EmailVerificationToken emailVerificationToken = emailVerificationTokenRepo.findById(UUID.fromString(token))
        .orElseThrow(() -> new BadCredentialsException("Invalid verification token"));

    // 2. Check if token is expired
    if (emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new BadCredentialsException("Verification token expired");
    }

    // 3. Update user status
    UserResponse user = userService.updateUserStatus(emailVerificationToken.getUserId(), UserStatus.ACTIVE);

    // 4. Delete token
    emailVerificationTokenRepo.delete(emailVerificationToken);

    // 5. Create welcome email data
    Map<String, Object> emailData = createEmailData(user, "Welcome to our shop!");

    // 6. Send welcome email
    emailProducer.sendWelcomeEmail(emailData);
    log.info("Welcome email sent to {}", user.getEmail());
    log.info("Welcome to our shop!");
  }

  /**
   * Re-sends a verification email
   * 
   * @param email the email address
   */
  @Override
  public void resendVerification(String email) {
    // 1. Check if user exists
    UserResponse user = userService.getUserByEmail(email);

    // 2. Check if user is active
    if (user.getStatus() == UserStatus.ACTIVE) {
      throw new BadRequestException("User already verified");
    }

    // 3. Create email data
    Map<String, Object> emailData = createEmailData(user, "Verify your email");

    // 4. Check if any token exists, if exists, delete all expired tokens
    List<EmailVerificationToken> emailVerificationTokens = emailVerificationTokenRepo.findByUserId(user.getId())
        .orElse(null);
    if (emailVerificationTokens != null) {
      var validToken = emailVerificationTokens.stream()
          .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now().plusHours(1)))
          .findFirst()
          .orElse(null);
      emailVerificationTokenRepo.deleteAll(
          emailVerificationTokens.stream()
              .filter(token -> !token.equals(validToken))
              .toList());
      // If valid token exists, add verification link to email data and send email
      if (validToken != null) {
        emailData.put(KafkaConstants.VERIFICATION_LINK_KEY,
            "http://localhost:8080/api/auth/verify?token=" + validToken.getId().toString());
        emailProducer.sendVerificationEmail(emailData);
        log.info("Verification email sent to {}", user.getEmail());
        log.info("Link: {}", "http://localhost:8080/api/auth/verify?token=" + validToken.getId().toString());
        return;
      }
    }

    // 5. Create new token
    EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
        .userId(user.getId())
        .expiryDate(LocalDateTime.now().plusHours(1))
        .build();
    emailVerificationTokenRepo.save(emailVerificationToken);

    // 6. Add verification link to email data
    emailData.put(KafkaConstants.VERIFICATION_LINK_KEY,
        "http://localhost:8080/api/auth/verify?token=" + emailVerificationToken.getId().toString());

    // 7. Send verification email
    emailProducer.sendVerificationEmail(emailData);
    log.info("Verification email sent to {}", user.getEmail());
    log.info("Link: {}", "http://localhost:8080/api/auth/verify?token=" + emailVerificationToken.getId().toString());
  }

  /**
   * Authenticates a user
   * 
   * @param request the login request DTO
   * @return the JWT token
   */
  @Override
  public String login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User user = (User) authentication.getPrincipal();
    log.info("User: {}", user);
    if (user.getStatus() != UserStatus.ACTIVE) {
      if (user.getStatus() == UserStatus.INACTIVE) {
        throw new BadRequestException("Please verify your email to login");
      } else {
        throw new BadRequestException("User is not active");
      }
    }
    return jwtTokenProvider.generateToken(user);
  }

  /**
   * Sends a password reset email
   * 
   * @param email the email address
   */
  @Override
  public void forgotPassword(String email) {
    // 1. Check if user exists
    UserResponse user = userService.getUserByEmail(email);

    // 2. Create email data
    Map<String, Object> emailData = createEmailData(user, "Reset your password");

    // 3. Check if any token exists
    List<ResetPasswordToken> resetPasswordTokens = resetPasswordTokenRepo.findByUserId(user.getId())
        .orElse(null);
    if (resetPasswordTokens != null) {
      var validToken = resetPasswordTokens.stream()
          .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now().plusHours(1)))
          .findFirst()
          .orElse(null);
      resetPasswordTokenRepo.deleteAll(
          resetPasswordTokens.stream()
              .filter(token -> !token.equals(validToken))
              .toList());
      if (validToken != null) {
        emailData.put(KafkaConstants.RESET_LINK_KEY,
            "http://localhost:8080/api/auth/reset-password?token=" + validToken.getId().toString());
        emailProducer.sendPasswordResetEmail(emailData);
        log.info("Reset password email sent to {}", user.getEmail());
        log.info("Link: {}", "http://localhost:8080/api/auth/reset-password?token=" + validToken.getId().toString());
        return;
      }
    }

    // 4. Create new token
    ResetPasswordToken resetPasswordToken = ResetPasswordToken.builder()
        .userId(user.getId())
        .expiryDate(LocalDateTime.now().plusHours(1))
        .build();
    resetPasswordTokenRepo.save(resetPasswordToken);

    // 5. Add reset password link to email data
    emailData.put(KafkaConstants.RESET_LINK_KEY,
        "http://localhost:8080/api/auth/reset-password?token=" + resetPasswordToken.getId().toString());

    // 6. Send reset password email
    emailProducer.sendPasswordResetEmail(emailData);
    log.info("Reset password email sent to {}", user.getEmail());
    log.info("Link: {}",
        "http://localhost:8080/api/auth/reset-password?token=" + resetPasswordToken.getId().toString());
  }

  /**
   * Resets a user's password
   * 
   * @param token       the reset password token
   * @param newPassword the new password
   */
  @Override
  public void resetPassword(String token, String newPassword) {
    // 1. Check if token exists
    ResetPasswordToken resetPasswordToken = resetPasswordTokenRepo.findById(UUID.fromString(token))
        .orElseThrow(() -> new BadCredentialsException("Invalid reset password token"));

    // 2. Check if token is expired
    if (resetPasswordToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new BadCredentialsException("Reset password token expired");
    }

    // 3. Update user password
    userService.updateUserPassword(resetPasswordToken.getUserId(), newPassword);

    // 4. Delete token
    resetPasswordTokenRepo.delete(resetPasswordToken);

    // TODO: Send email to notify that password is changed
  }

  /**
   * Changes a user's password
   * 
   * @param request the change password request
   */
  @Override
  public void changePassword(ChangePasswordRequest request) {
    // 1. Check if password is correct
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }

    // 2. Update user password
    userService.updateUserPassword(user.getId(), request.getNewPassword());

    // TODO: Send email to notify that password is changed
  }

  private Map<String, Object> createEmailData(UserResponse user, String subject) {
    Map<String, Object> emailData = new HashMap<>();
    emailData.put(KafkaConstants.RECIPIENT_EMAIL_KEY, user.getEmail());
    emailData.put(KafkaConstants.RECIPIENT_NAME_KEY, user.getDisplayName());
    emailData.put(KafkaConstants.SUBJECT_KEY, subject);
    return emailData;
  }
}
