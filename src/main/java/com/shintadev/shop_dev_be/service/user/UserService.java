package com.shintadev.shop_dev_be.service.user;

import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;

public interface UserService {

  UserResponse getUserById(Long id);

  UserResponse getUserByEmail(String email);

  UserResponse createUser(UserRequest request);

  UserResponse updateUser(UserRequest request);

  void deleteUser(Long id);

  boolean isEmailExists(String email);
}
