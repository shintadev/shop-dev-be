package com.shintadev.shop_dev_be.service.order.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.domain.dto.mapper.OrderMapper;
import com.shintadev.shop_dev_be.domain.dto.request.order.OrderRequest;
import com.shintadev.shop_dev_be.domain.dto.response.order.OrderResponse;
import com.shintadev.shop_dev_be.domain.model.entity.cart.Cart;
import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;
import com.shintadev.shop_dev_be.domain.model.entity.order.Order;
import com.shintadev.shop_dev_be.domain.model.entity.order.OrderItem;
import com.shintadev.shop_dev_be.domain.model.entity.order.Payment;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.entity.user.Address;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.order.OrderStatus;
import com.shintadev.shop_dev_be.domain.model.enums.order.PaymentStatus;
import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.cart.CartItemRepo;
import com.shintadev.shop_dev_be.repository.cart.CartRepo;
import com.shintadev.shop_dev_be.repository.order.OrderRepo;
import com.shintadev.shop_dev_be.repository.order.PaymentRepo;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.repository.user.AddressRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.service.order.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing orders
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepo orderRepo;
  private final OrderMapper orderMapper;
  private final ProductRepo productRepo;
  private final UserRepo userRepo;
  private final AddressRepo addressRepo;
  private final CartRepo cartRepo;
  private final CartItemRepo cartItemRepo;
  private final PaymentRepo paymentRepo;
  private final RedissonClient redissonClient;

  /**
   * Get all orders of a user
   * 
   * @param userId   the id of the user
   * @param pageable the pageable object
   * @return the list of orders
   */
  @Override
  @Transactional(readOnly = true)
  public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
    Page<Order> orders = orderRepo.findByUserId(userId, pageable);
    return orders.map(orderMapper::toOrderResponse);
  }

  /**
   * Get an order by its id
   * 
   * @param id the id of the order
   * @return the order
   */
  @Override
  public OrderResponse getOrderById(Long id) {
    Order order = orderRepo.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "id", id));
    return orderMapper.toOrderResponse(order);
  }

  /**
   * Get all orders of a user by their status
   * 
   * @param userId   the id of the user
   * @param status   the status of the orders
   * @param pageable the pageable object
   * @return the list of orders
   */
  @Override
  public Page<OrderResponse> getUserOrdersByStatus(Long userId, String status, Pageable pageable) {
    Page<Order> orders = orderRepo.findByUserIdAndStatus(userId, status, pageable);

    return orders.map(orderMapper::toOrderResponse);
  }

  /**
   * Get an order by its order number
   * 
   * @param orderNumber the order number
   * @return the order
   */
  @Override
  public OrderResponse getOrderByOrderNumber(String orderNumber) {
    Order order = orderRepo.findByOrderNumber(orderNumber)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ORDER, "orderNumber", orderNumber));

    return orderMapper.toOrderResponse(order);
  }

  /**
   * Get the recent orders of a user
   * 
   * @param userId the id of the user
   * @param limit  the limit of the orders
   * @return the list of orders
   */
  @Override
  public List<OrderResponse> getUserRecentOrders(Long userId, Integer limit) {
    List<Order> orders = orderRepo.findRecentOrdersByUserId(userId, limit);

    return orders.stream()
        .map(orderMapper::toOrderResponse)
        .toList();
  }

  /**
   * Create an order
   * 
   * @param userId       the id of the user
   * @param orderRequest the order request
   * @return the order
   */
  @Override
  public OrderResponse createOrder(Long userId, OrderRequest orderRequest) {
    // 1. Get lock
    String lockKey = "order:" + userId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 2. Try to acquire lock
      boolean acquired = lock.tryLock(15, 30, TimeUnit.SECONDS);
      if (!acquired) {
        throw new BadRequestException("Unable to create order. Please try again later");
      }

      try {
        // 3. Get user and shipping address
        User user = userRepo.findById(userId)
            .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.USER, "id", userId));

        Address shippingAddress = addressRepo.findById(orderRequest.getAddressId())
            .orElseThrow(
                () -> ResourceNotFoundException.create(ResourceName.ADDRESS, "id", orderRequest.getAddressId()));

        // 4. Check if shipping address belongs to user
        if (!shippingAddress.getUser().getId().equals(userId)) {
          throw new AccessDeniedException("You do not have permission to create order with this address");
        }

        // 5. Get cart
        Cart cart = cartRepo.findByUserId(userId)
            .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.CART, "userId", userId));

        if (cart.getItems().isEmpty()) {
          throw new BadRequestException("Cannot create order with empty cart");
        }

        // 6. Get checked out items
        Set<CartItem> checkedOutItems = new HashSet<>();

        // 7. If cart item ids are provided, get only the selected items
        if (orderRequest.getCartItemIds() != null && orderRequest.getCartItemIds().length > 0) {
          Set<Long> selectedItemIds = new HashSet<>(Arrays.asList(orderRequest.getCartItemIds()));

          for (CartItem item : cart.getItems()) {
            if (selectedItemIds.contains(item.getId())) {
              checkedOutItems.add(item);
            }
          }

          // 8. If no valid items are selected, throw an error
          if (checkedOutItems.isEmpty()) {
            throw new BadRequestException("No valid items selected for checkout");
          }
        } else {
          // 8. If no cart item ids are provided, get all items
          checkedOutItems.addAll(cart.getItems());
        }

        // 9. Verify and lock product stock
        verifyAndLockProductStock(checkedOutItems);

        // 10. Calculate subtotal, shipping cost and total
        BigDecimal subtotal = checkedOutItems.stream()
            .map(item -> {
              BigDecimal itemPrice = item.getProduct().getDiscountPrice() != null
                  ? item.getProduct().getDiscountPrice()
                  : item.getProduct().getPrice();

              return itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingCost = calculateShippingCost(checkedOutItems, shippingAddress);

        // 11. Create order
        Order order = Order.builder()
            .subtotal(subtotal)
            .shippingFee(shippingCost)
            .notes(orderRequest.getNotes())
            .status(OrderStatus.PENDING)
            .shippingAddress(shippingAddress)
            .user(user)
            .build();

        order = orderRepo.save(order);

        // 12. Create order items
        List<OrderItem> orderItems = createOrderItems(order, checkedOutItems);
        order.setItems(new HashSet<>(orderItems));

        // 13. Create payment
        Payment payment = Payment.builder()
            .order(order)
            .amount(order.getTotalPrice())
            .status(PaymentStatus.PENDING)
            .build();

        payment = paymentRepo.save(payment);

        // 14. Update product stock
        updateProductStock(checkedOutItems);

        // 15. Remove items from cart
        removeItemsFromCart(cart, checkedOutItems);

        // TODO: Send order created message to customer

        initiatePaymentProcess(payment);

        return orderMapper.toOrderResponse(order);
      } finally {
        // 12. Release lock
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BadRequestException("Operation interrupted. Please try again later.");
    }
  }

  /**
   * Update the status of an order
   * 
   * @param id     the id of the order
   * @param status the status of the order
   * @return the order
   */
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

  /**
   * Cancel an order
   * 
   * @param userId the id of the user
   * @param id     the id of the order
   * @return the order
   */
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

  /**
   * Get the count of orders of a user
   * 
   * @param userId the id of the user
   * @return the count of orders
   */
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

  /**
   * Calculate the shipping cost of an order
   * 
   * @param cartItems       the items in the cart
   * @param shippingAddress the shipping address
   * @return the shipping cost
   */
  private BigDecimal calculateShippingCost(Set<CartItem> cartItems, Address shippingAddress) {
    BigDecimal baseShippingCost = BigDecimal.TEN;

    int totalQuantity = cartItems.stream()
        .mapToInt(CartItem::getQuantity)
        .sum();

    BigDecimal itemBasedShippingCost = new BigDecimal(Math.ceil(totalQuantity / 5.0) * 2);

    return baseShippingCost.add(itemBasedShippingCost);
  }

  /**
   * Restore the stock of a product
   * 
   * @param order the order
   */
  private void restoreProductStock(Order order) {
    for (OrderItem item : order.getItems()) {
      Product product = item.getProduct();
      product.setStock(product.getStock() + item.getQuantity());
      productRepo.save(product);
    }
  }

  /**
   * Create order items
   * 
   * @param order     the order
   * @param cartItems the items in the cart
   * @return the list of order items
   */
  private List<OrderItem> createOrderItems(Order order, Set<CartItem> cartItems) {
    List<OrderItem> orderItems = new ArrayList<>();

    for (CartItem cartItem : cartItems) {
      Product product = cartItem.getProduct();

      OrderItem orderItem = OrderItem.builder()
          .order(order)
          .product(product)
          .quantity(cartItem.getQuantity())
          .build();

      orderItems.add(orderItem);
    }

    return orderItems;
  }

  /**
   * Update the stock of a product
   * 
   * @param cartItems the items in the cart
   */
  private void updateProductStock(Set<CartItem> cartItems) {
    for (CartItem cartItem : cartItems) {
      Product product = cartItem.getProduct();
      product.setStock(product.getStock() - cartItem.getQuantity());
      productRepo.save(product);
    }
  }

  /**
   * Remove items from cart
   * 
   * @param cart      the cart
   * @param cartItems the items in the cart
   */
  private void removeItemsFromCart(Cart cart, Set<CartItem> cartItems) {
    for (CartItem cartItem : cartItems) {
      cart.getItems().remove(cartItem);
      cartItemRepo.delete(cartItem);
    }

    // Re-calculate cart total
    BigDecimal total = cart.getItems().stream()
        .map(item -> item.getProduct().getDiscountPrice() != null
            ? item.getProduct().getDiscountPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            : item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    cart.setTotalPrice(total);
    cartRepo.save(cart);
  }

  /**
   * Initiate the payment process
   * 
   * @param payment the payment
   */
  private void initiatePaymentProcess(Payment payment) {

  }
}
