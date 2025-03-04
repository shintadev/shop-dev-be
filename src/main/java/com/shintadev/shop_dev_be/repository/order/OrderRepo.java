package com.shintadev.shop_dev_be.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.order.Order;

/**
 * Repository for managing orders
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

}
