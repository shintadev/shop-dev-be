package com.shintadev.shop_dev_be.constant.kafka;

public record KafkaTopic() {
  public static final String EMAIL_NOTIFICATIONS_TOPIC = "email-notifications";
  public static final String PAYMENT_PROCESSING_TOPIC = "payment-processing";
}
