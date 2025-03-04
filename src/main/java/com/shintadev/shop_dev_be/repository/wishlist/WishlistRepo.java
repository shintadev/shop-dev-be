package com.shintadev.shop_dev_be.repository.wishlist;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.wishlist.Wishlist;

import jakarta.persistence.LockModeType;

/**
 * Repository for managing wishlists
 */
@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

  Optional<Wishlist> findByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId")
  Optional<Wishlist> findByUserIdForUpdate(Long userId);
}
