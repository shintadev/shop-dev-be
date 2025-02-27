package com.shintadev.shop_dev_be.domain.dto.request.user;

import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 128, message = "Email must be less than 128 characters")
  private String email;

  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
  private String lastName;

  @Size(min = 2, max = 128, message = "Display name must be between 2 and 128 characters")
  private String displayName;

  @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 digits")
  @Pattern(regexp = "^\\d{10,15}$", message = "Phone must contain only numbers")
  private String phone;

  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.INACTIVE;
}
