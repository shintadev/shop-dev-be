package com.shintadev.shop_dev_be.service.user.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.domain.dto.mapper.UserMapper;
import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.EmailVerificationToken;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;
import com.shintadev.shop_dev_be.repository.user.EmailVerificationTokenRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.service.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepo userRepo;

  private final UserMapper userMapper;

  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  @Override
  public UserResponse getUserById(Long id) {
    User user = userRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return userMapper.toUserResponse(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserResponse getUserByEmail(String email) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return userMapper.toUserResponse(user);
  }

  @Override
  public UserResponse createUser(UserRequest request) {
    // 1. Check if email exists
    if (userRepo.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Email already exists");
    }
    // 2. Map request to user
    User user = userMapper.toUser(request);
    log.info("User: {}", user);
    // 3. Encode password
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    // 4. Save user
    User savedUser = userRepo.save(user);
    log.info("Saved user: {}", savedUser);
    // 5. Map user to response
    return userMapper.toUserResponse(savedUser);
  }

  @Override
  public UserResponse updateUser(UserRequest request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
  }

  @Override
  public UserResponse updateUserStatus(Long id, UserStatus status) {
    User user = userRepo.findByIdForUpdate(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setStatus(status);
    return userMapper.toUserResponse(userRepo.save(user));
  }

  @Override
  public void deleteUser(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }

  @Transactional(readOnly = true)
  @Override
  public boolean isEmailExists(String email) {
    return userRepo.existsByEmail(email);
  }

}
