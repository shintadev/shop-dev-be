package com.shintadev.shop_dev_be.service.cart.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.domain.dto.mapper.CartMapper;
import com.shintadev.shop_dev_be.domain.dto.request.cart.CartItemRequest;
import com.shintadev.shop_dev_be.domain.dto.response.cart.CartResponse;
import com.shintadev.shop_dev_be.domain.model.entity.cart.Cart;
import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.cart.CartRepo;
import com.shintadev.shop_dev_be.repository.cart.CartItemRepo;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.service.cart.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

  private final CartRepo cartRepo;
  private final CartItemRepo cartItemRepo;
  private final CartMapper cartMapper;
  private final ProductRepo productRepo;
  private final UserRepo userRepo;
  private final RedissonClient redissonClient;

  @Override
  @Transactional(readOnly = true)
  public CartResponse getCart(Long userId) {
    Cart cart = getUserCart(userId);
    return cartMapper.toCartResponse(cart);
  }

  @Override
  public CartResponse addItemToCart(Long userId, CartItemRequest request) {
    // 1. Get lock
    String lockKey = "cart:" + userId + ":product:" + request.getProductId();
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 2. Try to acquire lock
      boolean acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
      if (!acquired) {
        throw new BadRequestException("Unable to add item to cart. Please try again later");
      }

      try {
        // 3. Get cart
        Cart cart = getUserCart(userId);

        // 4. Get product
        Product product = productRepo.findById(request.getProductId())
            .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", request.getProductId().toString()));

        // 5. Check product status
        if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
          throw new BadRequestException("Product is not available");
        }

        // 6. Check stock
        if (product.getStock() < request.getQuantity()) {
          throw new BadRequestException("Insufficient stock");
        }

        // 7. Check if item already exists in cart
        Optional<CartItem> cartItemOpt = cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId());

        // 8. If item exists, update quantity, otherwise create new item
        CartItem cartItem;
        if (cartItemOpt.isPresent()) {
          cartItem = cartItemOpt.get();
          cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
          cartItem = CartItem.builder()
              .cart(cart)
              .product(product)
              .quantity(request.getQuantity())
              .build();
        }

        // 9. Save cart item
        cartItemRepo.save(cartItem);

        // 10. Update cart total price
        updateCartTotalPrice(cart);

        // 11. Return cart response
        return cartMapper.toCartResponse(cart);
      } finally {
        // 12. Release lock
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BadRequestException("Operation interrupted. Please try again later.");
    }
  }

  @Override
  public CartResponse updateItemInCart(Long userId, CartItemRequest request) {
    // 1. Get lock
    String lockKey = "cart:" + userId + ":product:" + request.getProductId();
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 2. Try to acquire lock
      boolean acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
      if (!acquired) {
        throw new BadRequestException("Unable to update item in cart. Please try again later");
      }

      try {
        // 3. Get cart
        Cart cart = getUserCart(userId);

        // 4. Get cart item
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        // 5. Check if product is available
        Product product = productRepo.findById(request.getProductId())
            .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", request.getProductId().toString()));

        // 6. Check stock
        if (product.getStock() < request.getQuantity()) {
          throw new BadRequestException("Insufficient stock");
        }

        // 7. Update cart item
        cartMapper.updateCartItemFromRequest(request, cartItem);

        // 8. Save cart item
        cartItemRepo.save(cartItem);

        // 9. Update cart total price
        updateCartTotalPrice(cart);

        // 10. Return cart response
        return cartMapper.toCartResponse(cart);
      } finally {
        // 11. Release lock
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BadRequestException("Operation interrupted. Please try again later.");
    }
  }

  @Override
  public CartResponse removeItemFromCart(Long userId, Long productId) {
    // 1. Get lock
    String lockKey = "cart:" + userId + ":product:" + productId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 2. Try to acquire lock
      boolean acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
      if (!acquired) {
        throw new BadRequestException("Unable to remove item from cart. Please try again later");
      }

      try {
        // 3. Get cart
        Cart cart = getUserCart(userId);

        // 4. Get cart item
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        // 5. Delete cart item
        cart.getItems().remove(cartItem);
        cartItemRepo.delete(cartItem);

        // 6. Update cart total price
        updateCartTotalPrice(cart);

        // 7. Return cart response
        return cartMapper.toCartResponse(cart);
      } finally {
        // 8. Release lock
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BadRequestException("Operation interrupted. Please try again later.");
    }
  }

  @Override
  public CartResponse clearCart(Long userId) {
    // 1. Get lock
    String lockKey = "cart:" + userId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 2. Try to acquire lock
      boolean acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
      if (!acquired) {
        throw new BadRequestException("Unable to clear cart. Please try again later");
      }

      try {
        // 3. Get cart
        Cart cart = getUserCart(userId);

        // 4. Delete all cart items
        cart.getItems().clear();

        // 5. Update cart total price
        cart.setTotalPrice(BigDecimal.ZERO);

        // 6. Save cart
        cartRepo.save(cart);

        // 7. Return cart response
        return cartMapper.toCartResponse(cart);
      } finally {
        // 8. Release lock
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BadRequestException("Operation interrupted. Please try again later.");
    }
  }

  @Override
  public Long getCartItemCount(Long userId) {
    return cartItemRepo.countByUserId(userId);
  }

  private Cart getUserCart(Long userId) {
    // 1. Check if user exists
    User user = userRepo.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create("User", "id", userId.toString()));

    // 2. Get or create cart
    return cartRepo.findByUserId(userId)
        .orElseGet(() -> {
          Cart newCart = Cart.builder()
              .user(user)
              .build();
          return cartRepo.save(newCart);
        });
  }

  private void updateCartTotalPrice(Cart cart) {
    // 1. Calculate total price
    BigDecimal totalPrice = cart.getItems().stream()
        .map(item -> item.getProduct().getDiscountPrice() != null
            ? item.getProduct().getDiscountPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            : item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalPrice(totalPrice);

    // 2. Save cart
    cartRepo.save(cart);
  }
}
