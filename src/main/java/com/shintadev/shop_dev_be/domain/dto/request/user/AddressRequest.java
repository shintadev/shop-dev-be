package com.shintadev.shop_dev_be.domain.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

  @NotBlank(message = "Recipient name is required")
  @Size(min = 2, max = 128, message = "Recipient name must be between 2 and 128 characters")
  private String recipientName;

  @NotBlank(message = "Phone number is required")
  @Size(min = 10, max = 16, message = "Phone number must be between 10 and 16 digits")
  @Pattern(regexp = "^\\d{10,16}$", message = "Phone number must contain only numbers")
  private String phoneNumber;

  @NotBlank(message = "Address line 1 is required")
  @Size(min = 2, max = 255, message = "Address line 1 must be between 2 and 255 characters")
  private String addressLine1;

  @Size(max = 255, message = "Address line 2 must be less than 255 characters")
  private String addressLine2;

  @NotBlank(message = "Ward is required")
  @Size(min = 2, max = 128, message = "Ward must be between 2 and 128 characters")
  private String ward;

  @NotBlank(message = "District is required")
  @Size(min = 2, max = 128, message = "District must be between 2 and 128 characters")
  private String district;

  @NotBlank(message = "Province/City is required")
  @Size(min = 2, max = 128, message = "Province/City must be between 2 and 128 characters")
  private String provinceCity;

  @NotBlank(message = "Postal code is required")
  @Size(min = 5, max = 10, message = "Postal code must be between 5 and 10 digits")
  private String postalCode;

  private boolean isDefault;
}
