package com.shintadev.shop_dev_be.domain.dto.response.auth;

import java.util.Set;

import com.shintadev.shop_dev_be.domain.model.enums.user.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

  private String token;
  @Builder.Default
  private String type = "Bearer";
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String displayName;
  private Set<RoleName> roles;
}
