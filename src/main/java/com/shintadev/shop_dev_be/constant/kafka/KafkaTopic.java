package com.shintadev.shop_dev_be.constant.kafka;

public record KafkaTopic() {
  /*
   * Email Topics
   */
  public static final String VERIFICATION_EMAILS_TOPIC = "verification-emails";
  public static final String PASSWORD_RESET_EMAILS_TOPIC = "password-reset-emails";
  public static final String WELCOME_EMAILS_TOPIC = "welcome-emails";

  /*
   * Order Topics
   */
  public static final String ORDER_CREATED_TOPIC = "order-created";
  public static final String ORDER_UPDATED_TOPIC = "order-updated";
  public static final String ORDER_CANCELLED_TOPIC = "order-cancelled";
}
