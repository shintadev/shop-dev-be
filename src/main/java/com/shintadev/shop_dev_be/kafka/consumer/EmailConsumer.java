package com.shintadev.shop_dev_be.kafka.consumer;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.KafkaConstants;
import com.shintadev.shop_dev_be.constant.kafka.KafkaTopic;
import com.shintadev.shop_dev_be.service.common.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Consumer for email events
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer {

  private final EmailService emailService;

  /**
   * Consumes verification emails
   * 
   * @param emailData the email data
   */
  @KafkaListener(topics = KafkaTopic.VERIFICATION_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumeVerificationEmail(Map<String, Object> emailData) {
    log.info("Consuming verification email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendVerificationEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY),
        (String) emailData.get(KafkaConstants.VERIFICATION_LINK_KEY));
  }

  /**
   * Consumes password reset emails
   * 
   * @param emailData the email data
   */
  @KafkaListener(topics = KafkaTopic.PASSWORD_RESET_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumePasswordResetEmail(Map<String, Object> emailData) {
    log.info("Consuming password reset email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendPasswordResetEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY),
        (String) emailData.get(KafkaConstants.RESET_LINK_KEY));
  }

  /**
   * Consumes welcome emails
   * 
   * @param emailData the email data
   */
  @KafkaListener(topics = KafkaTopic.WELCOME_EMAILS_TOPIC, groupId = KafkaConstants.EMAIL_GROUP)
  public void consumeWelcomeEmail(Map<String, Object> emailData) {
    log.info("Consuming welcome email for: {}", emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY));
    emailService.sendWelcomeEmail(
        (String) emailData.get(KafkaConstants.RECIPIENT_EMAIL_KEY),
        (String) emailData.get(KafkaConstants.RECIPIENT_NAME_KEY),
        (String) emailData.get(KafkaConstants.SUBJECT_KEY));
  }
}
