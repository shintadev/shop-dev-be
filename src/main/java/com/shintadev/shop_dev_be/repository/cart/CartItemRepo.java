package com.shintadev.shop_dev_be.repository.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;

/**
 * Repository for managing cart items
 */
@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

  @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
  Long countByUserId(Long userId);
}
