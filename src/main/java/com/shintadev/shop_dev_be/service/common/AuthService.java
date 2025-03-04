package com.shintadev.shop_dev_be.service.common;

import com.shintadev.shop_dev_be.domain.dto.request.auth.ChangePasswordRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.LoginRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.RegisterRequest;

public interface AuthService {

  void register(RegisterRequest request);

  void verify(String token);

  void resendVerification(String email);

  String login(LoginRequest request);

  void forgotPassword(String email);

  void resetPassword(String token, String newPassword);

  void changePassword(ChangePasswordRequest request);

}
