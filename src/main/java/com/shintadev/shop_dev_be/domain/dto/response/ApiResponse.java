package com.shintadev.shop_dev_be.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse {
  /**
   * Result of the response. True if the request is successful, false otherwise.
   */
  private boolean success;
  /**
   * Message of the response.
   */
  private String message;
  /**
   * Data of the response. For example, a list of products, a single product, etc.
   */
  private Object data;
}
