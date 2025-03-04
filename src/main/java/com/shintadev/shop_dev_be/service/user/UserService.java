package com.shintadev.shop_dev_be.service.user;

import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;

public interface UserService {

  UserResponse getUserById(Long id);

  UserResponse getUserByEmail(String email);

  UserResponse createUser(UserRequest request);

  UserResponse updateUser(UserRequest request);

  UserResponse updateUserStatus(Long id, UserStatus status);

  void updateUserPassword(Long id, String newPassword);

  void deleteUser(Long id);

  boolean isEmailExists(String email);
}
