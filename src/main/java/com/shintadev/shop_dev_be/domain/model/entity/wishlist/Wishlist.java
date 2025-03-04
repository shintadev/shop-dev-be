package com.shintadev.shop_dev_be.domain.model.entity.wishlist;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "wishlist")
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<WishlistItem> items = new HashSet<>();

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}