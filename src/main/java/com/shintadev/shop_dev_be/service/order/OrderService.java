package com.shintadev.shop_dev_be.service.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shintadev.shop_dev_be.domain.dto.request.order.OrderRequest;
import com.shintadev.shop_dev_be.domain.dto.response.order.OrderResponse;
import com.shintadev.shop_dev_be.domain.model.enums.order.OrderStatus;

public interface OrderService {

  Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

  OrderResponse getOrderById(Long id);

  Page<OrderResponse> getUserOrdersByStatus(Long userId, String status, Pageable pageable);

  OrderResponse getOrderByOrderNumber(String orderNumber);

  List<OrderResponse> getUserRecentOrders(Long userId, Integer limit);

  OrderResponse createOrder(Long userId, OrderRequest orderRequest);

  OrderResponse updateOrderStatus(Long id, OrderStatus status);

  OrderResponse cancelOrder(Long userId, Long id);

  Long getOrderCount(Long userId);
}
