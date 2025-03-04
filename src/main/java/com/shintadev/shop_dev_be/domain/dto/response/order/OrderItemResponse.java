package com.shintadev.shop_dev_be.domain.dto.response.order;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
  private Long id;
  private Long productId;
  private String productName;
  private String productImage;
  private BigDecimal price;
  private Integer quantity;
  private BigDecimal subtotal;
}
