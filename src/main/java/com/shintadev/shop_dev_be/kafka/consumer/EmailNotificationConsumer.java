package com.shintadev.shop_dev_be.kafka.consumer;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.kafka.EmailNotificationType;
import com.shintadev.shop_dev_be.constant.kafka.KafkaMessageDataAttribute;
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
public class EmailNotificationConsumer {

  private final EmailService emailService;

  /**
   * Consumes email notification messages from the email notifications topic
   * 
   * @param messageData the message data
   */
  @KafkaListener(topics = KafkaTopic.EMAIL_NOTIFICATIONS_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consume(Map<String, Object> messageData, Acknowledgment acknowledgment) {
    try {
      log.info("Consuming email notification: {}", messageData.get(KafkaMessageDataAttribute.EMAIL_TYPE_KEY));

      String type = (String) messageData.get(KafkaMessageDataAttribute.EMAIL_TYPE_KEY);
      String recipientEmail = (String) messageData.get(KafkaMessageDataAttribute.RECIPIENT_EMAIL_KEY);
      String recipientName = (String) messageData.get(KafkaMessageDataAttribute.RECIPIENT_NAME_KEY);
      String subject = (String) messageData.get(KafkaMessageDataAttribute.SUBJECT_KEY);

      switch (type) {
        case EmailNotificationType.VERIFICATION:
          emailService.sendVerificationEmail(
              recipientEmail,
              recipientName,
              subject,
              (String) messageData.get(KafkaMessageDataAttribute.VERIFICATION_LINK_KEY));
          break;
        case EmailNotificationType.PASSWORD_RESET:
          emailService.sendPasswordResetEmail(
              recipientEmail,
              recipientName,
              subject,
              (String) messageData.get(KafkaMessageDataAttribute.RESET_LINK_KEY));
          break;
        case EmailNotificationType.WELCOME:
          emailService.sendWelcomeEmail(recipientEmail, recipientName, subject);
          break;
        case EmailNotificationType.ORDER_CONFIRMATION:
          emailService.sendOrderConfirmationEmail(
              recipientEmail,
              recipientName,
              subject);
          break;
        case EmailNotificationType.ORDER_STATUS_UPDATE:
          emailService.sendOrderStatusUpdateEmail(
              recipientEmail,
              recipientName,
              subject,
              (String) messageData.get(KafkaMessageDataAttribute.ORDER_STATUS_KEY));
          break;
        case EmailNotificationType.ORDER_CANCELLED:
          emailService.sendOrderCancelledEmail(
              recipientEmail,
              recipientName,
              subject);
          break;
        default:
          log.warn("Invalid email type: {}", type);
      }

      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("Error consuming email notification: {}", e.getMessage(), e);
      // acknowledgment.nack(Duration.ofMillis(1000)); // Nack the message after 1 sec
    }
  }
}
