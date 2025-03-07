package com.shintadev.shop_dev_be.constant.kafka;

public record EmailNotificationType() {
  public static final String VERIFICATION = "verification";
  public static final String PASSWORD_RESET = "password_reset";
  public static final String WELCOME = "welcome";
  public static final String ORDER_CONFIRMATION = "order_confirmation";
  public static final String ORDER_STATUS_UPDATE = "order_status_update";
  public static final String ORDER_CANCELLED = "order_cancelled";
}
