package com.shintadev.shop_dev_be.repository.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;

import jakarta.persistence.LockModeType;

/**
 * Repository for managing cart items
 */
@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

  @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
  List<CartItem> findByCartId(Long cartId);

  @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
  Optional<CartItem> findByCartIdAndProductIdForUpdate(Long cartId, Long productId);

  @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
  Long countByUserId(Long userId);
}
