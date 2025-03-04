package com.shintadev.shop_dev_be.domain.model.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.shintadev.shop_dev_be.domain.model.entity.user.Address;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.order.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_number", nullable = false, unique = true)
  private String orderNumber;

  @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotal;

  @Column(name = "shipping_fee", precision = 10, scale = 2, nullable = false)
  private BigDecimal shippingFee;

  @Column(name = "tax", precision = 10, scale = 2, nullable = false)
  private BigDecimal tax;

  @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
  private BigDecimal totalPrice;

  @Column(name = "notes", length = 255)
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<OrderItem> items = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipping_address_id", nullable = false)
  private Address shippingAddress;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private Payment payment;

  @CreationTimestamp
  @Column(name = "order_at", updatable = false)
  private LocalDateTime orderAt;

  @Column(name = "payment_at")
  private LocalDateTime paymentAt;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @PrePersist
  public void prePersist() {
    // Generate order number
    this.orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    // Calculate order price
    this.tax = this.subtotal.multiply(BigDecimal.valueOf(0.1));
    this.totalPrice = this.subtotal.add(this.shippingFee).add(this.tax);
  }

  @PreUpdate
  public void calculateOrderPrice() {
    this.tax = this.subtotal.multiply(BigDecimal.valueOf(0.1));
    this.totalPrice = this.subtotal.add(this.shippingFee).add(this.tax);
  }
}
