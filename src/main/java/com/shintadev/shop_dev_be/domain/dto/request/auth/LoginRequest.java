package com.shintadev.shop_dev_be.domain.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email address")
  @Size(max = 128, message = "Email must be less than 128 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  private String password;
}
