package com.shintadev.shop_dev_be.domain.dto.request.auth;

import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends UserRequest {

  // @NotBlank
  // @Email
  // @Size(max = 128, message = "Email must be less than 128 characters")
  // private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  // @Pattern(regexp =
  // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
  // message = "Password must contain at least one uppercase letter, one lowercase
  // letter, one number, and one special character")
  private String password;

  // @NotBlank
  // @Size(min = 2, max = 64, message = "First name must be between 2 and 64
  // characters")
  // private String firstName;

  // @NotBlank
  // @Size(min = 2, max = 64, message = "Last name must be between 2 and 64
  // characters")
  // private String lastName;

  // @Size(min = 2, max = 128, message = "Display name must be between 2 and 128
  // characters")
  // private String displayName;

  // @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 digits")
  // @Pattern(regexp = "^\\d{10,15}$", message = "Phone must contain only
  // numbers")
  // private String phone;

  final private UserStatus status = UserStatus.INACTIVE;
}
