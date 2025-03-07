package com.shintadev.shop_dev_be.constant.kafka;

public record KafkaMessageKey() {
  public static final String EMAIL_VERIFICATION = "email-verification";
  public static final String PASSWORD_RESET = "password-reset";
  public static final String WELCOME_EMAIL = "welcome-email";
  public static final String ORDER_CREATED = "order-created";
  public static final String ORDER_UPDATED = "order-updated";
  public static final String ORDER_CANCELLED = "order-cancelled";
  public static final String PAYMENT_SUCCESS = "payment-success";
  public static final String PAYMENT_FAILED = "payment-failed";
}
