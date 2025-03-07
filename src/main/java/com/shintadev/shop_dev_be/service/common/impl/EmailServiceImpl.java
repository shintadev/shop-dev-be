package com.shintadev.shop_dev_be.service.common.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.shintadev.shop_dev_be.service.common.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  /**
   * Sends a verification email asynchronously
   * 
   * @param to               the email address
   * @param name             the name
   * @param subject          the subject
   * @param verificationLink the verification link
   */
  @Async("emailExecutor")
  @Override
  public void sendVerificationEmail(
      String to,
      String name,
      String subject,
      String verificationLink) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("verificationLink", verificationLink);

      String htmlContent = templateEngine.process("email/verification", context);

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("Verification email sent to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send verification email", e);
    }
  }

  /**
   * Sends a password reset email asynchronously
   * 
   * @param to        the email address
   * @param name      the name
   * @param subject   the subject
   * @param resetLink the reset link
   */
  @Async("emailExecutor")
  @Override
  public void sendPasswordResetEmail(
      String to,
      String name,
      String subject,
      String resetLink) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("resetLink", resetLink);

      String htmlContent = templateEngine.process("email/password-reset", context);

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("Password reset email sent to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send password reset email", e);
    }
  }

  /**
   * Sends a welcome email asynchronously
   * 
   * @param to      the email address
   * @param name    the name
   * @param subject the subject
   */
  @Async("emailExecutor")
  @Override
  public void sendWelcomeEmail(
      String to,
      String name,
      String subject) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

      Context context = new Context();
      context.setVariable("name", name);

      String htmlContent = templateEngine.process("email/welcome", context);

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("Welcome email sent to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send welcome email", e);
    }
  }

  @Override
  public void sendOrderConfirmationEmail(String to, String name, String subject) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendOrderConfirmationEmail'");
  }

  @Override
  public void sendOrderStatusUpdateEmail(String to, String name, String subject, String orderStatus) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendOrderStatusUpdateEmail'");
  }

  @Override
  public void sendOrderCancelledEmail(String to, String name, String subject) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'sendOrderCancelledEmail'");
  }
}
