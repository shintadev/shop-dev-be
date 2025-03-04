package com.shintadev.shop_dev_be.domain.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.shintadev.shop_dev_be.domain.model.enums.order.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
  private Long id;
  private BigDecimal amount;
  private PaymentStatus status;
  private String transactionId;
  private String paymentDetails;
  private LocalDateTime paymentDate;
  private Long orderId;
  private String orderNumber;
  private LocalDateTime updatedAt;
}
