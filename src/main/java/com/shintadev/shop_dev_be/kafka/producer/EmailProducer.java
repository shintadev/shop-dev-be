package com.shintadev.shop_dev_be.kafka.producer;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.KafkaConstants;
import com.shintadev.shop_dev_be.constant.kafka.KafkaTopic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Producer for email events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

  private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

  /**
   * Sends a verification email
   * 
   * @param emailData the email data
   */
  public void sendVerificationEmail(Map<String, Object> emailData) {
    log.info("Sending verification email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaTopic.VERIFICATION_EMAILS_TOPIC, emailData);
  }

  /**
   * Sends a password reset email
   * 
   * @param emailData the email data
   */
  public void sendPasswordResetEmail(Map<String, Object> emailData) {
    log.info("Sending password reset email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaTopic.PASSWORD_RESET_EMAILS_TOPIC, emailData);
  }

  /**
   * Sends a welcome email
   * 
   * @param emailData the email data
   */
  public void sendWelcomeEmail(Map<String, Object> emailData) {
    log.info("Sending welcome email to: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    kafkaTemplate.send(KafkaTopic.WELCOME_EMAILS_TOPIC, emailData);
  }
}
