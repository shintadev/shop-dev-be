package com.shintadev.shop_dev_be.domain.model.enums.order;

public enum OrderStatus {
  PENDING,
  PROCESSING,
  SHIPPED,
  DELIVERED,
  CANCELLED,
  RETURNED,
  REFUNDED,
  PAYMENT_PENDING,
  PAYMENT_FAILED,
  PAYMENT_COMPLETED
}
