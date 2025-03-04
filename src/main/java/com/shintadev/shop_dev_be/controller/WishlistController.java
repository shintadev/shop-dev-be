package com.shintadev.shop_dev_be.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.service.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;

/**
 * WishlistController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-04
 */
@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
public class WishlistController {

  private final WishlistService wishlistService;

  /**
   * Get the wishlist of the user
   * 
   * @param user the authenticated user
   * @return the wishlist of the user
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getWishlist(@AuthenticationPrincipal User user) {
    var wishlist = wishlistService.getWishlist(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Wishlist retrieved successfully")
            .data(wishlist)
            .build());
  }

  @GetMapping("/paged")
  public ResponseEntity<ApiResponse> getPagedWishlist(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size);
    var wishlist = wishlistService.getPagedWishlist(user.getId(), pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Wishlist retrieved successfully")
            .data(wishlist)
            .build());
  }

  /**
   * Add a product to the wishlist
   * 
   * @param user      the authenticated user
   * @param productId the ID of the product
   * @return the updated wishlist
   */
  @PostMapping("/add")
  public ResponseEntity<ApiResponse> addProductToWishlist(
      @AuthenticationPrincipal User user,
      @RequestParam Long productId) {
    var wishlist = wishlistService.addProductToWishlist(user.getId(), productId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .message("Product added to wishlist successfully")
            .data(wishlist)
            .build());
  }

  @GetMapping("/check")
  public ResponseEntity<ApiResponse> isProductInWishlist(
      @AuthenticationPrincipal User user,
      @RequestParam Long productId) {
    var isInWishlist = wishlistService.isProductInWishlist(user.getId(), productId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Product in wishlist check successfully")
            .data(isInWishlist)
            .build());
  }

  /**
   * Remove a product from the wishlist
   * 
   * @param user      the authenticated user
   * @param productId the ID of the product
   * @return the response entity
   */
  @DeleteMapping("/remove")
  public ResponseEntity<ApiResponse> removeProductFromWishlist(
      @AuthenticationPrincipal User user,
      @RequestParam Long productId) {
    wishlistService.removeProductFromWishlist(user.getId(), productId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Product removed from wishlist successfully")
            .build());
  }

  /**
   * Clear the wishlist
   * 
   * @param user the authenticated user
   * @return the response entity
   */
  @DeleteMapping("/clear")
  public ResponseEntity<ApiResponse> clearWishlist(@AuthenticationPrincipal User user) {
    wishlistService.clearWishlist(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Wishlist cleared successfully")
            .build());
  }

  /**
   * Get the number of items in the wishlist
   * 
   * @param user the authenticated user
   * @return the number of items in the wishlist
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse> getWishlistCount(@AuthenticationPrincipal User user) {
    var count = wishlistService.getWishlistCount(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .message("Wishlist count retrieved successfully")
            .data(count)
            .build());
  }
}
