package com.shintadev.shop_dev_be.domain.dto.response.user;

import java.util.Set;

import com.shintadev.shop_dev_be.domain.model.entity.user.Role;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

  private Long id;

  private String email;

  private String firstName;

  private String lastName;

  private String displayName;

  private String phone;

  private String avatarUrl;

  private Set<Role> roles;

  private UserStatus status;
}
