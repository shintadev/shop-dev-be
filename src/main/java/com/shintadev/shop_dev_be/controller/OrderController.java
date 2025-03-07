package com.shintadev.shop_dev_be.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.order.OrderRequest;
import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.service.order.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * OrderController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  /**
   * Get all orders of a user
   * 
   * @param user the authenticated user
   * @param page the page number
   * @param size the page size
   * @return the list of orders
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getAllUserOrders(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var orders = orderService.getOrdersByUserId(user.getId(), pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Orders fetched successfully")
            .data(orders)
            .build());
  }

  /**
   * Get an order by its id
   * 
   * @param id the id of the order
   * @return the order
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long id) {
    var order = orderService.getOrderById(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Order fetched successfully")
            .data(order)
            .build());
  }

  /**
   * Get all orders of a user by their status
   * 
   * @param user   the authenticated user
   * @param status the status of the orders
   * @param page   the page number
   * @param size   the page size
   * @return the list of orders
   */
  @GetMapping("/status/{status}")
  public ResponseEntity<ApiResponse> getOrdersByStatus(
      @AuthenticationPrincipal User user,
      @PathVariable String status,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var orders = orderService.getUserOrdersByStatus(user.getId(), status, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Orders fetched successfully")
            .data(orders)
            .build());
  }

  /**
   * Get an order by its order number
   * 
   * @param orderNumber the order number
   * @return the order
   */
  @GetMapping("/tracking/{orderNumber}")
  public ResponseEntity<ApiResponse> getOrderTracking(
      @PathVariable String orderNumber) {
    var order = orderService.getOrderByOrderNumber(orderNumber);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Order tracking fetched successfully")
            .data(order)
            .build());
  }

  /**
   * Get the recent orders of a user
   * 
   * @param user  the authenticated user
   * @param limit the limit of the orders
   * @return the list of orders
   */
  @GetMapping("/recent")
  public ResponseEntity<ApiResponse> getRecentOrders(
      @AuthenticationPrincipal User user,
      @RequestParam(defaultValue = "5") Integer limit) {
    var orders = orderService.getUserRecentOrders(user.getId(), limit);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Recent orders fetched successfully")
            .data(orders)
            .build());
  }

  /**
   * Create an order
   * 
   * @param user         the authenticated user
   * @param orderRequest the order request
   * @return the order
   */
  @PostMapping
  public ResponseEntity<ApiResponse> createOrder(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody OrderRequest orderRequest) {
    var order = orderService.createOrder(user.getId(), orderRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .success(true)
            .message("Order created successfully")
            .data(order)
            .build());
  }

  /**
   * Cancel an order
   * 
   * @param user the authenticated user
   * @param id   the id of the order
   * @return the order
   */
  @PutMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse> cancelOrder(
      @AuthenticationPrincipal User user,
      @PathVariable Long id) {
    var order = orderService.cancelOrder(user.getId(), id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Order cancelled successfully")
            .data(order)
            .build());
  }

  /**
   * Get the count of orders of a user
   * 
   * @param user the authenticated user
   * @return the count of orders
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse> getOrderCount(@AuthenticationPrincipal User user) {
    var count = orderService.getOrderCount(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Order count fetched successfully")
            .data(count)
            .build());
  }
}