package com.shintadev.shop_dev_be.service.cart;

import com.shintadev.shop_dev_be.domain.dto.request.cart.CartItemRequest;
import com.shintadev.shop_dev_be.domain.dto.response.cart.CartResponse;

public interface CartService {

  CartResponse getCart(Long userId);

  CartResponse addItemToCart(Long userId, CartItemRequest request);

  CartResponse updateItemInCart(Long userId, CartItemRequest request);

  CartResponse removeItemFromCart(Long userId, Long productId);

  CartResponse clearCart(Long userId);

  Long getCartItemCount(Long userId);
}
