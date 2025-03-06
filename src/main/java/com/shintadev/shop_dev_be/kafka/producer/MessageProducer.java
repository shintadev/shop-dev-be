package com.shintadev.shop_dev_be.kafka.producer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.shintadev.shop_dev_be.constant.kafka.KafkaMessageDataAttribute;
import com.shintadev.shop_dev_be.constant.kafka.KafkaTopic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Producer for email events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

  private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

  /**
   * Sends an email notification message
   * 
   * @param messageData the message data
   */
  public void sendEmailNotification(Map<String, Object> messageData) {
    log.info("Sending email notification to: {}", messageData.get(KafkaMessageDataAttribute.EMAIL_TYPE_KEY));
    sendMessageWithCallback(KafkaTopic.EMAIL_NOTIFICATIONS_TOPIC,
        messageData.get(KafkaMessageDataAttribute.RECIPIENT_EMAIL_KEY), messageData);
  }

  /**
   * Sends a payment processing message
   * 
   * @param messageData the message data
   */
  public void sendPaymentProcessingMessage(Map<String, Object> messageData) {
    log.info("Sending payment processing message to: {}", messageData.get(KafkaMessageDataAttribute.ORDER_ID_KEY));
    sendMessageWithCallback(KafkaTopic.PAYMENT_PROCESSING_TOPIC,
        messageData.get(KafkaMessageDataAttribute.ORDER_ID_KEY), messageData);
  }

  private void sendMessageWithCallback(String topic, Object key, Map<String, Object> messageData) {
    CompletableFuture<SendResult<String, Map<String, Object>>> future = kafkaTemplate.send(topic, key.toString(),
        messageData);

    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.debug("Message sent successfully to topic {}: key={}, partition={}, offset={}",
            topic, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
      } else {
        log.error("Failed to send message to topic {} with key {}", topic, key, ex);
      }
    });
  }
}
