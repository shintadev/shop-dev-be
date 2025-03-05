package com.shintadev.shop_dev_be.constant;

public record KafkaConstants() {

  /*
   * Email Groups
   */
  public static final String EMAIL_GROUP = "shop-dev-email-group";

  /*
   * Email Keys
   */
  public static final String RECIPIENT_EMAIL_KEY = "recipientEmail";
  public static final String RECIPIENT_NAME_KEY = "recipientName";
  public static final String SUBJECT_KEY = "subject";
  public static final String VERIFICATION_LINK_KEY = "verificationLink";
  public static final String RESET_LINK_KEY = "resetLink";
}
