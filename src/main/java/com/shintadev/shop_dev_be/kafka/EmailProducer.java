package com.shintadev.shop_dev_be.kafka;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.KafkaConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

  private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

  public void sendVerificationEmail(Map<String, Object> emailData) {
    log.info("Sending verification email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaConstants.VERIFICATION_EMAILS_TOPIC, emailData);
  }

  public void sendPasswordResetEmail(Map<String, Object> emailData) {
    log.info("Sending password reset email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaConstants.PASSWORD_RESET_EMAILS_TOPIC, emailData);
  }

  public void sendOrderConfirmationEmail(Map<String, Object> emailData) {
    log.info("Sending order confirmation email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaConstants.ORDER_CONFIRMATION_EMAILS_TOPIC, emailData);
  }
}
