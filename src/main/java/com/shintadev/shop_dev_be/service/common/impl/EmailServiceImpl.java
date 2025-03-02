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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

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

  @Async("emailExecutor")
  @Override
  public void sendOrderConfirmationEmail(
      String to,
      String name,
      String subject,
      Long orderId,
      String orderDate,
      Double totalAmount) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("orderId", orderId);
      context.setVariable("orderDate", orderDate);
      context.setVariable("totalAmount", totalAmount);

      String htmlContent = templateEngine.process("email/order-confirmation", context);

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
      log.info("Order confirmation email sent to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send order confirmation email", e);
    }
  }
}
