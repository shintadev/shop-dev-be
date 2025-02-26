package com.shintadev.shop_dev_be.service.common.impl;

import org.springframework.stereotype.Service;

import com.shintadev.shop_dev_be.domain.dto.request.auth.EmailVerifyRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.LoginRequest;
import com.shintadev.shop_dev_be.domain.dto.request.auth.RegisterRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.service.common.AuthService;
import com.shintadev.shop_dev_be.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserService userService;

  @Override
  public void register(RegisterRequest request) {
    UserResponse user = userService.createUser(request);

    // TODO: Send verification email
  }

  @Override
  public void verify(EmailVerifyRequest request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'verify'");
  }

  @Override
  public void resendVerification(String email) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resendVerification'");
  }

  @Override
  public String login(LoginRequest request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'login'");
  }

  @Override
  public void forgotPassword(String email) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'forgotPassword'");
  }

  @Override
  public void resetPassword(String token, String email) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resetPassword'");
  }

  @Override
  public void changePassword(String token, String newPassword) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
  }

  @Override
  public void logout(String token) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'logout'");
  }

}
