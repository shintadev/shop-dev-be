package com.shintadev.shop_dev_be.domain.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailVerifyRequest {

  @NotBlank
  @Email
  @Size(max = 128, message = "Email must be less than 128 characters")
  private String email;

  @NotBlank
  @Size(min = 6, max = 6, message = "Code must be 6 characters")
  private String code;
}
