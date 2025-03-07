package com.shintadev.shop_dev_be.constant.kafka;

public record KafkaMessageDataAttribute() {
  /*
   * Email Notification Keys
   */
  public static final String EMAIL_TYPE_KEY = "type";
  public static final String RECIPIENT_EMAIL_KEY = "recipientEmail";
  public static final String RECIPIENT_NAME_KEY = "recipientName";
  public static final String SUBJECT_KEY = "subject";
  public static final String VERIFICATION_LINK_KEY = "verificationLink";
  public static final String RESET_LINK_KEY = "resetLink";
  public static final String ORDER_STATUS_KEY = "orderStatus";

  /*
   * Payment Processing Keys
   */
  public static final String ORDER_ID_KEY = "orderId";
  public static final String PAYMENT_STATUS_KEY = "paymentStatus";
}
