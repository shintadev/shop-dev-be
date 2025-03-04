package com.shintadev.shop_dev_be.service.wishlist.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.domain.dto.mapper.WishlistMapper;
import com.shintadev.shop_dev_be.domain.dto.response.wishlist.WishlistResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.entity.wishlist.Wishlist;
import com.shintadev.shop_dev_be.domain.model.entity.wishlist.WishlistItem;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.repository.wishlist.WishlistItemRepo;
import com.shintadev.shop_dev_be.repository.wishlist.WishlistRepo;
import com.shintadev.shop_dev_be.service.wishlist.WishlistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing wishlists
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

  private final WishlistRepo wishlistRepo;
  private final WishlistItemRepo wishlistItemRepo;
  private final WishlistMapper wishlistMapper;
  private final ProductRepo productRepo;
  private final UserRepo userRepo;

  /**
   * Get the wishlist of the user
   * 
   * @param userId the ID of the user
   * @return the wishlist dto of the user
   */
  @Override
  @Transactional(readOnly = true)
  public WishlistResponse getWishlist(Long userId) {
    // 1. Check if user exists
    User user = userRepo.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.USER, "id", userId.toString()));

    // 2. Get or create wishlist
    Wishlist wishlist = wishlistRepo.findByUserId(userId)
        .orElseGet(() -> {
          Wishlist newWishlist = Wishlist.builder()
              .user(user)
              .build();
          return wishlistRepo.save(newWishlist);
        });

    // 3. Return wishlist
    return wishlistMapper.toWishlistResponse(wishlist);
  }

  // ???
  @Override
  @Transactional(readOnly = true)
  public Page<WishlistResponse> getPagedWishlist(Long userId, Pageable pageable) {
    Wishlist wishlist = getUserWishlist(userId);

    Page<WishlistItem> pagedItems = wishlistItemRepo.findByWishlistId(wishlist.getId(), pageable);

    List<WishlistResponse> responses = pagedItems.getContent().stream()
        .map(item -> {
          Wishlist singleItemWishlist = Wishlist.builder()
              .id(item.getWishlist().getId())
              .user(item.getWishlist().getUser())
              .updatedAt(item.getWishlist().getUpdatedAt())
              .build();
          singleItemWishlist.getItems().add(item);

          return wishlistMapper.toWishlistResponse(singleItemWishlist);
        })
        .toList();

    return new PageImpl<>(responses, pageable, pagedItems.getTotalElements());
  }

  /**
   * Add a product to the wishlist
   * 
   * @param userId    the ID of the user
   * @param productId the ID of the product
   * @return the updated wishlist
   */
  @Override
  public WishlistResponse addProductToWishlist(Long userId, Long productId) {
    // 1. Get wishlist
    Wishlist wishlist = getUserWishlist(userId);

    // 2. Get product
    Product product = productRepo.findById(productId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.PRODUCT, "id", productId.toString()));

    // 3. Check if product is already in wishlist
    Optional<WishlistItem> wishlistItemOpt = wishlistItemRepo.findByWishlistIdAndProductId(wishlist.getId(), productId);

    // 4. If product is already in wishlist, remove it, otherwise add it
    WishlistItem wishlistItem;
    if (wishlistItemOpt.isPresent()) {
      wishlistItem = wishlistItemOpt.get();
      wishlist.getItems().remove(wishlistItem);
    } else {
      wishlistItem = WishlistItem.builder()
          .wishlist(wishlist)
          .product(product)
          .build();
      wishlist.getItems().add(wishlistItem);
    }

    // 5. Save wishlist
    wishlist = wishlistRepo.save(wishlist);

    // 6. Return wishlist
    return wishlistMapper.toWishlistResponse(wishlist);
  }

  /**
   * Check if a product is in the wishlist
   * 
   * @param userId    the ID of the user
   * @param productId the ID of the product
   * @return true if the product is in the wishlist, false otherwise
   */
  @Override
  public boolean isProductInWishlist(Long userId, Long productId) {
    // 1. Get wishlist
    Wishlist wishlist = getUserWishlist(userId);

    // 2. Check if wishlist is empty
    if (wishlist.getItems().isEmpty()) {
      return false;
    }

    // 3. Check if product is in wishlist
    return wishlistItemRepo.findByWishlistIdAndProductId(wishlist.getId(), productId).isPresent();
  }

  /**
   * Remove a product from the wishlist
   * 
   * @param userId    the ID of the user
   * @param productId the ID of the product
   * @return the updated wishlist
   */
  @Override
  public WishlistResponse removeProductFromWishlist(Long userId, Long productId) {
    // 1. Get wishlist
    Wishlist wishlist = getUserWishlist(userId);

    // 2. Get wishlist item
    WishlistItem wishlistItem = wishlistItemRepo.findByWishlistIdAndProductId(wishlist.getId(), productId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.WISHLIST_ITEM, "id", productId.toString()));

    // 3. Remove wishlist item
    wishlist.getItems().remove(wishlistItem);

    // 4. Save wishlist
    wishlist = wishlistRepo.save(wishlist);

    // 5. Return wishlist
    return wishlistMapper.toWishlistResponse(wishlist);
  }

  /**
   * Clear the wishlist
   * 
   * @param userId the ID of the user
   * @return the updated wishlist
   */
  @Override
  public WishlistResponse clearWishlist(Long userId) {
    // 1. Get wishlist
    Wishlist wishlist = getUserWishlist(userId);

    // 2. Clear wishlist
    wishlist.getItems().clear();

    // 3. Save wishlist
    wishlist = wishlistRepo.save(wishlist);

    // 4. Return wishlist
    return wishlistMapper.toWishlistResponse(wishlist);
  }

  /**
   * Get the number of items in the wishlist
   * 
   * @param userId the ID of the user
   * @return the number of items in the wishlist
   */
  @Override
  public Long getWishlistCount(Long userId) {
    return wishlistItemRepo.countByUserId(userId);
  }

  /**
   * Get the wishlist of the user
   * 
   * @param userId the ID of the user
   * @return the wishlist entity of the user
   */
  private Wishlist getUserWishlist(Long userId) {
    // 1. Check if user exists
    User user = userRepo.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.USER, "id", userId.toString()));

    // 2. Get or create wishlist
    return wishlistRepo.findByUserIdForUpdate(userId)
        .orElseGet(() -> {
          Wishlist newWishlist = Wishlist.builder()
              .user(user)
              .build();
          return wishlistRepo.save(newWishlist);
        });
  }
}