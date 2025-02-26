package com.shintadev.shop_dev_be.domain.model.entity.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", length = 64, nullable = false)
  private String firstName;

  @Column(name = "last_name", length = 64, nullable = false)
  private String lastName;

  @Column(name = "display_name", length = 128)
  private String displayName;

  @Column(name = "email", length = 128, nullable = false, unique = true)
  private String email;

  @Column(name = "password", length = 64, nullable = false)
  private String password;

  @Column(name = "phone", length = 16)
  private String phone;

  @Column(name = "avatar_url", length = 255)
  private String avatarUrl;

  // @Column(name = "role", nullable = false)
  // private String role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private UserStatus status = UserStatus.INACTIVE;

  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval =
  // true)
  // private List<Address> addresses = new ArrayList<>();

  // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  // private Cart cart;

  // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  // private Wishlist wishlist;

  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  // private List<Order> orders = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    if (displayName == null) {
      displayName = firstName + " " + lastName;
    }
  }
}
