package com.shintadev.shop_dev_be.domain.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.shintadev.shop_dev_be.domain.dto.response.user.AddressResponse;
import com.shintadev.shop_dev_be.domain.model.enums.order.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
  private Long id;
  private String orderNumber;
  private BigDecimal subtotal;
  private BigDecimal shippingFee;
  private BigDecimal tax;
  private BigDecimal totalPrice;
  private String notes;
  private OrderStatus status;
  private List<OrderItemResponse> items;
  private AddressResponse shippingAddress;
  private Long userId;
  private String userName;
  private PaymentResponse payment;
  private LocalDateTime orderAt;
  private LocalDateTime paymentAt;
  private LocalDateTime shippedAt;
  private LocalDateTime deliveredAt;
  private LocalDateTime cancelledAt;
}
