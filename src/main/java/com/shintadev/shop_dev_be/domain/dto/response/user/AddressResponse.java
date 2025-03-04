package com.shintadev.shop_dev_be.domain.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
  private Long id;
  private String userId;
  private String recipientName;
  private String phoneNumber;
  private String addressLine1;
  private String addressLine2;
  private String ward;
  private String district;
  private String provinceCity;
  private String postalCode;
  private boolean isDefault;
  private String formattedAddress;
}
