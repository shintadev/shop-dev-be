package com.shintadev.shop_dev_be.domain.dto.request.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

  @NotBlank(message = "Current password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  private String password;

  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters")
  // @Pattern(regexp =
  // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
  // message = "Password must contain at least one uppercase letter, one lowercase
  // letter, one number, and one special character")
  private String newPassword;

  @AssertTrue(message = "New password must be different from current password")
  private boolean isNewPasswordDifferent() {
    if (password == null || newPassword == null) {
      return true;
    }
    return !password.equals(newPassword);
  }
}
