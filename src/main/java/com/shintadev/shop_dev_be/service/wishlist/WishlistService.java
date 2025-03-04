package com.shintadev.shop_dev_be.service.wishlist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shintadev.shop_dev_be.domain.dto.response.wishlist.WishlistResponse;

public interface WishlistService {

  WishlistResponse getWishlist(Long userId);

  Page<WishlistResponse> getPagedWishlist(Long userId, Pageable pageable);

  WishlistResponse addProductToWishlist(Long userId, Long productId);

  boolean isProductInWishlist(Long userId, Long productId);

  WishlistResponse removeProductFromWishlist(Long userId, Long productId);

  WishlistResponse clearWishlist(Long userId);

  Long getWishlistCount(Long userId);
}
