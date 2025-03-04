package com.shintadev.shop_dev_be.repository.wishlist;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.wishlist.WishlistItem;

/**
 * Repository for managing wishlist items
 */
@Repository
public interface WishlistItemRepo extends JpaRepository<WishlistItem, Long> {

  Page<WishlistItem> findByWishlistId(Long wishlistId, Pageable pageable);

  Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId, Long productId);

  @Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.wishlist.user.id = :userId")
  Long countByUserId(Long userId);
}
