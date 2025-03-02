package com.shintadev.shop_dev_be.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaConstants {

  /*
   * Email Topics
   */
  public static final String VERIFICATION_EMAILS_TOPIC = "verification-emails";
  public static final String PASSWORD_RESET_EMAILS_TOPIC = "password-reset-emails";
  public static final String ORDER_CONFIRMATION_EMAILS_TOPIC = "order-confirmation-emails";

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
  public static final String ORDER_ID_KEY = "orderId";
  public static final String ORDER_DATE_KEY = "orderDate";
  public static final String TOTAL_AMOUNT_KEY = "totalAmount";
}
