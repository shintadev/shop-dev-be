package com.shintadev.shop_dev_be.domain.dto.request.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters")
  private String name;

  @Size(max = 5000, message = "Description must be less than 5000 characters")
  private String description;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private BigDecimal price;

  private BigDecimal discountPrice;

  @NotNull(message = "Stock is required")
  @Min(value = 0, message = "Stock must be greater than 0")
  private Integer stock;

  @NotNull(message = "Images are required")
  @Size(min = 1, message = "At least one image is required")
  private List<String> images;

  @NotNull(message = "Featured is required")
  private boolean featured;

  @NotNull(message = "Category ID is required")
  private Long categoryId;
}
