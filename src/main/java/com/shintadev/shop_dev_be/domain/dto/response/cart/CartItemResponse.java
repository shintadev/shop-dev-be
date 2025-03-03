package com.shintadev.shop_dev_be.domain.dto.response.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
  private Long id;
  private Long productId;
  private String productName;
  private String productSlug;
  private List<String> productImages;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private int quantity;
  private BigDecimal subTotal;
  private LocalDateTime updatedAt;
}
