package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.cart.CartItemRequest;
import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.service.cart.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * CartController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-03
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  /**
   * Get the cart of the user
   * 
   * @param user the authenticated user
   * @return the cart of the user
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getCart(@AuthenticationPrincipal User user) {
    var cart = cartService.getCart(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Cart retrieved successfully")
            .data(cart)
            .build());
  }

  /**
   * Add an item to the cart
   * 
   * @param user    the authenticated user
   * @param request the request containing the product ID and quantity
   * @return the updated cart
   */
  @PostMapping("/add")
  public ResponseEntity<ApiResponse> addItemToCart(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody CartItemRequest request) {
    var cartItem = cartService.addItemToCart(user.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .message("Item added to cart successfully")
            .data(cartItem)
            .build());
  }

  /**
   * Update an item in the cart
   * 
   * @param user    the authenticated user
   * @param request the request containing the product ID and quantity
   * @return the updated cart
   */
  @PutMapping("/update")
  public ResponseEntity<ApiResponse> updateItemInCart(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody CartItemRequest request) {
    var cartItem = cartService.updateItemInCart(user.getId(), request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Item updated in cart successfully")
            .data(cartItem)
            .build());
  }

  /**
   * Remove an item from the cart
   * 
   * @param user      the authenticated user
   * @param productId the ID of the product
   * @return the updated cart
   */
  @DeleteMapping("/remove")
  public ResponseEntity<ApiResponse> removeItemFromCart(
      @AuthenticationPrincipal User user,
      @RequestParam Long productId) {
    var cart = cartService.removeItemFromCart(user.getId(), productId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Item removed from cart successfully")
            .data(cart)
            .build());
  }

  /**
   * Clear the cart
   * 
   * @param user the authenticated user
   * @return the updated cart
   */
  @DeleteMapping("/clear")
  public ResponseEntity<ApiResponse> clearCart(@AuthenticationPrincipal User user) {
    var cart = cartService.clearCart(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Cart cleared successfully")
            .data(cart)
            .build());
  }

  /**
   * Get the number of items in the cart
   * 
   * @param user the authenticated user
   * @return the number of items in the cart
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse> getCartItemCount(@AuthenticationPrincipal User user) {
    var count = cartService.getCartItemCount(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Cart item count retrieved successfully")
            .data(count)
            .build());
  }

}
