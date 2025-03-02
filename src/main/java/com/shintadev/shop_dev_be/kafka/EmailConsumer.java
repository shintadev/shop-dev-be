package com.shintadev.shop_dev_be.kafka;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.KafkaConstants;
import com.shintadev.shop_dev_be.service.common.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = KafkaConstants.VERIFICATION_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumeVerificationEmail(Map<String, Object> emailData) {
    log.info("Consuming verification email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendVerificationEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY),
        (String) emailData.get(KafkaConstants.VERIFICATION_LINK_KEY));
  }

  @KafkaListener(topics = KafkaConstants.PASSWORD_RESET_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumePasswordResetEmail(Map<String, Object> emailData) {
    log.info("Consuming password reset email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendPasswordResetEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY),
        (String) emailData.get(KafkaConstants.RESET_LINK_KEY));
  }

  @KafkaListener(topics = KafkaConstants.ORDER_CONFIRMATION_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumeOrderConfirmationEmail(Map<String, Object> emailData) {
    log.info("Consuming order confirmation email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendOrderConfirmationEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY),
        (Long) emailData.get(KafkaConstants.ORDER_ID_KEY),
        (String) emailData.get(KafkaConstants.ORDER_DATE_KEY),
        (Double) emailData.get(KafkaConstants.TOTAL_AMOUNT_KEY));
  }
}
