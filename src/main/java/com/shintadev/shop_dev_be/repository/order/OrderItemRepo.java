package com.shintadev.shop_dev_be.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.order.OrderItem;

/**
 * Repository for managing order items
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
}
