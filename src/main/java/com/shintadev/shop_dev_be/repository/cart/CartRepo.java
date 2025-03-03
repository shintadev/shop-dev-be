package com.shintadev.shop_dev_be.repository.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.shintadev.shop_dev_be.domain.model.entity.cart.Cart;

import jakarta.persistence.LockModeType;

/**
 * Repository for managing carts
 */
@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

  @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
  Optional<Cart> findByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
  Optional<Cart> findByUserIdForUpdate(Long userId);

  @Query("SELECT c FROM Cart c JOIN FETCH c.items WHERE c.user.id = :userId")
  Optional<Cart> findByUserIdWithItems(Long userId);
}
