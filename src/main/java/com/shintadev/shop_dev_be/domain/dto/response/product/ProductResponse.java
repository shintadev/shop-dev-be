package com.shintadev.shop_dev_be.domain.dto.response.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
  private Long id;
  private String name;
  private String slug;
  private String description;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private Integer stock;
  private List<String> images;
  private boolean featured;
  private boolean inStock;
  private boolean onSale;
  private Long categoryId;
  private String categoryName;
  private String categorySlug;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
