package com.shintadev.shop_dev_be.domain.dto.response.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private String slug;
  private String description;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private int stock;
  private List<String> images;
  private boolean featured;
  // private CategoryResponse category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
