package com.shintadev.shop_dev_be.domain.model.entity.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "products", indexes = {
    @Index(name = "idx_product_slug", columnList = "slug")
})
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 128, nullable = false)
  private String name;

  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "price", precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(name = "discount_price", precision = 10, scale = 2)
  private BigDecimal discountPrice;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @ElementCollection
  @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "image_url")
  @Builder.Default
  private List<String> images = new ArrayList<>();

  @Column(name = "status", nullable = false)
  private ProductStatus status;

  @Column(name = "is_featured", nullable = false)
  private Boolean featured;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  @PreUpdate
  public void checkDiscountPrice() {
    if (discountPrice != null && discountPrice.compareTo(price) >= 0) {
      discountPrice = null;
    }
  }
}
