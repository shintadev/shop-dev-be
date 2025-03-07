package com.shintadev.shop_dev_be.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.order.Order;

/**
 * Repository for managing orders
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

  Page<Order> findByUserId(Long userId, Pageable pageable);

  Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

  Optional<Order> findByOrderNumber(String orderNumber);

  @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderAt DESC LIMIT :limit")
  List<Order> findRecentOrdersByUserId(Long userId, Integer limit);

  @Query("SELECT o FROM Order o WHERE o.id = :id")
  Optional<Order> findByIdForUpdate(Long id);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
  Long countByUserId(Long userId);
}
