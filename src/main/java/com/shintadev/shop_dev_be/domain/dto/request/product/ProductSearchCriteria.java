package com.shintadev.shop_dev_be.domain.dto.request.product;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductSearchCriteria {
  private String keyword;
  private Long categoryId;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private Boolean inStock = true;
  private Boolean onSale = false;
}
