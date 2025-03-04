package com.shintadev.shop_dev_be.domain.dto.request.auth;

import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends UserRequest {

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
  private String password;

  final private UserStatus status = UserStatus.INACTIVE;
}
