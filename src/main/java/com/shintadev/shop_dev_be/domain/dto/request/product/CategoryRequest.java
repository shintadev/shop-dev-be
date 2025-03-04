package com.shintadev.shop_dev_be.domain.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 128, message = "Name must be between 2 and 128 characters")
  private String name;

  private String description;

  private String image;

  private boolean active = true;

  private Long parentId;

  private List<Long> childrenIds;
}
