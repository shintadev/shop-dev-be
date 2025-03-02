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

  void sendOrderConfirmationEmail(
      String to,
      String name,
      String subject,
      Long orderId,
      String orderDate,
      Double totalAmount);
}
