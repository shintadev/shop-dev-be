package com.shintadev.shop_dev_be.domain.dto.request.order;

import lombok.Data;

@Data
public class OrderRequest {

  private Long addressId;

  private String notes;

  private Long[] cartItemIds;
}
