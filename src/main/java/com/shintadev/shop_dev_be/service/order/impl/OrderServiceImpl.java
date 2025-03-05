package com.shintadev.shop_dev_be.service.order.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.domain.dto.mapper.OrderMapper;
import com.shintadev.shop_dev_be.domain.dto.request.order.OrderRequest;
import com.shintadev.shop_dev_be.domain.dto.response.order.OrderResponse;
import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;
import com.shintadev.shop_dev_be.domain.model.entity.order.Order;
import com.shintadev.shop_dev_be.domain.model.entity.order.OrderItem;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.entity.user.Address;
import com.shintadev.shop_dev_be.domain.model.enums.order.OrderStatus;
import com.shintadev.shop_dev_be.domain.model.enums.order.PaymentStatus;
import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.order.OrderRepo;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.service.order.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepo orderRepo;
  private final OrderMapper orderMapper;
  private final ProductRepo productRepo;

  @Override
  @Transactional(readOnly = true)
  public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
    Page<Order> orders = orderRepo.findByUserId(userId, pageable);
    return orders.map(orderMapper::toOrderResponse);
  }

  @Override
  public OrderResponse getOrderById(Long id) {
    Order order = orderRepo.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "id", id));
    return orderMapper.toOrderResponse(order);
  }

  @Override
  public Page<OrderResponse> getUserOrdersByStatus(Long userId, String status, Pageable pageable) {
    Page<Order> orders = orderRepo.findByUserIdAndStatus(userId, status, pageable);

    return orders.map(orderMapper::toOrderResponse);
  }

  @Override
  public OrderResponse getOrderByOrderNumber(String orderNumber) {
    Order order = orderRepo.findByOrderNumber(orderNumber)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "orderNumber", orderNumber));

    return orderMapper.toOrderResponse(order);
  }

  @Override
  public List<OrderResponse> getUserRecentOrders(Long userId, Integer limit) {
    List<Order> orders = orderRepo.findRecentOrdersByUserId(userId, limit);

    return orders.stream()
        .map(orderMapper::toOrderResponse)
        .toList();
  }

  @Override
  public OrderResponse createOrder(Long userId, OrderRequest orderRequest) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createOrder'");
  }

  @Override
  public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
    Order order = orderRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "id", id));

    order.setStatus(status);

    switch (status) {
      case SHIPPED:
        order.setShippedAt(LocalDateTime.now());
        break;
      case DELIVERED:
        order.setDeliveredAt(LocalDateTime.now());
        break;
      case CANCELLED:
        order.setCancelledAt(LocalDateTime.now());
        break;
      default:
        break;
    }

    order = orderRepo.save(order);

    // TODO: Send order status updated message to customer

    return orderMapper.toOrderResponse(order);
  }

  @Override
  public OrderResponse cancelOrder(Long userId, Long id) {
    Order order = orderRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "id", id));

    if (!order.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You do not have permission to cancel this order");
    }

    if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED
        || order.getStatus() == OrderStatus.CANCELLED) {
      log.error("Order is in {} state", order.getStatus());
      throw new BadRequestException("Order cannot be cancelled in this state");
    }

    order.setStatus(OrderStatus.CANCELLED);
    order.setCancelledAt(LocalDateTime.now());

    restoreProductStock(order);

    if (order.getPayment() != null &&
        (order.getPayment().getStatus() == PaymentStatus.PENDING ||
            order.getPayment().getStatus() == PaymentStatus.PROCESSING)) {
      order.getPayment().setStatus(PaymentStatus.CANCELLED);
      order.getPayment().setUpdatedAt(LocalDateTime.now());
    }

    order = orderRepo.save(order);

    // TODO: Send order cancelled message to customer

    return orderMapper.toOrderResponse(order);
  }

  @Override
  public Long getOrderCount(Long userId) {
    return orderRepo.countByUserId(userId);
  }

  private void verifyAndLockProductStock(Set<CartItem> cartItems) {
    List<String> outOfStockProducts = new ArrayList<>();

    for (CartItem cartItem : cartItems) {
      Product product = cartItem.getProduct();

      if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
        outOfStockProducts.add(product.getName() + " is not available");
        continue;
      }

      if (product.getStock() < cartItem.getQuantity()) {
        outOfStockProducts.add(product.getName() + " has only " + product.getStock() + " in stock");
        continue;
      }
    }

    if (!outOfStockProducts.isEmpty()) {
      throw new BadRequestException("Cannot complete order: "
          + String.join(",\n", outOfStockProducts));
    }
  }

  private BigDecimal calculateShippingCost(Set<CartItem> cartItems, Address shippingAddress) {
    BigDecimal baseShippingCost = BigDecimal.TEN;

    int totalQuantity = cartItems.stream()
        .mapToInt(CartItem::getQuantity)
        .sum();

    BigDecimal itemBasedShippingCost = new BigDecimal(Math.ceil(totalQuantity / 5.0) * 2);

    return baseShippingCost.add(itemBasedShippingCost);
  }

  private void restoreProductStock(Order order) {
    for (OrderItem item : order.getItems()) {
      Product product = item.getProduct();
      product.setStock(product.getStock() + item.getQuantity());
      productRepo.save(product);
    }
  }
}
