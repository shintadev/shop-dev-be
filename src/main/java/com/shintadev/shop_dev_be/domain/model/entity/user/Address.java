package com.shintadev.shop_dev_be.domain.model.entity.user;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "recipient_name", length = 128, nullable = false)
  private String recipientName;

  @Column(name = "phone_number", length = 16, nullable = false)
  private String phoneNumber;

  @Column(name = "address_line_1", length = 255, nullable = false)
  private String addressLine1;

  @Column(name = "address_line_2", length = 255)
  private String addressLine2;

  @Column(name = "ward", length = 128, nullable = false)
  private String ward;

  @Column(name = "district", length = 128, nullable = false)
  private String district;

  @Column(name = "province_city", length = 128, nullable = false)
  private String provinceCity;

  @Column(name = "postal_code", length = 16, nullable = false)
  private String postalCode;

  @Column(name = "is_default")
  private boolean isDefault;
}
