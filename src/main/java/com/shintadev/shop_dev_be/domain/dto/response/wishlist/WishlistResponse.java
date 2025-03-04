package com.shintadev.shop_dev_be.domain.dto.response.wishlist;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {
  private Long id;
  private String userId;
  private List<WishlistItemResponse> items;
  private int itemCount;
  private LocalDateTime updatedAt;
}
