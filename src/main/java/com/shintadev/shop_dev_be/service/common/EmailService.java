package com.shintadev.shop_dev_be.service.common;

public interface EmailService {

  void sendVerificationEmail(
      String to,
      String name,
      String subject,
      String verificationLink);

  void sendPasswordResetEmail(
      String to,
      String name,
      String subject,
      String resetLink);

  void sendWelcomeEmail(
      String to,
      String name,
      String subject);

  void sendOrderConfirmationEmail(
      String to,
      String name,
      String subject);

  void sendOrderStatusUpdateEmail(
      String to,
      String name,
      String subject,
      String orderStatus);

  void sendOrderCancelledEmail(
      String to,
      String name,
      String subject);
}
