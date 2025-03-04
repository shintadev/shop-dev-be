package com.shintadev.shop_dev_be.domain.dto.response.wishlist;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistItemResponse {
  private Long id;
  private String productId;
  private String productName;
  private String productSlug;
  private List<String> productImages;
  private BigDecimal productPrice;
  private BigDecimal productDiscountPrice;
  private boolean inStock;
  private LocalDateTime createdAt;
}
