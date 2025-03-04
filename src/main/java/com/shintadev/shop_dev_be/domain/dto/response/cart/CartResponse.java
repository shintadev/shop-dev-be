package com.shintadev.shop_dev_be.domain.dto.response.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {

  private Long id;
  private Long userId;
  private List<CartItemResponse> items;
  private int itemCount;
  private BigDecimal totalPrice;
  private LocalDateTime updatedAt;
}
