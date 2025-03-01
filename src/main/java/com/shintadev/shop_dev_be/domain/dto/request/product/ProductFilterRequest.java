package com.shintadev.shop_dev_be.domain.dto.request.product;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductFilterRequest {
  private Long categoryId;
  private String search;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private String sortBy;
  private String sortOrder;
}
