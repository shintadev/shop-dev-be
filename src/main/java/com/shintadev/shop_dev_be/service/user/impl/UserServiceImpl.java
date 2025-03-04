package com.shintadev.shop_dev_be.service.user.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.domain.dto.mapper.UserMapper;
import com.shintadev.shop_dev_be.domain.dto.request.user.UserRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.UserResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.domain.model.enums.user.UserStatus;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.user.RoleRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.service.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing users
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepo userRepo;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepo roleRepo;

  /**
   * Gets a user by their ID
   * 
   * @param id the ID of the user
   * @return the user response DTO
   */
  @Transactional(readOnly = true)
  @Override
  public UserResponse getUserById(Long id) {
    User user = userRepo.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.create("User", "id", id));

    return userMapper.toUserResponse(user);
  }

  /**
   * Gets a user by their email
   * 
   * @param email the email of the user
   * @return the user response DTO
   */
  @Transactional(readOnly = true)
  @Override
  public UserResponse getUserByEmail(String email) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> ResourceNotFoundException.create("User", "email", email));

    return userMapper.toUserResponse(user);
  }

  /**
   * Creates a new user
   * 
   * @param request the user request DTO
   * @return the user response DTO
   */
  @Override
  public UserResponse createUser(UserRequest request) {
    // 1. Check if email exists
    if (userRepo.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already exists");
    }
    // 2. Map request to user
    User user = userMapper.toUser(request);
    log.info("User: {}", user);
    // 3. Encode password
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    // 4. Set roles
    var roles = request.getRoles()
        .stream()
        .map(roleRepo::findByName)
        .map(Optional::get)
        .collect(Collectors.toSet());
    user.setRoles(roles);
    // 5. Save user
    User savedUser = userRepo.save(user);
    log.info("Saved user: {}", savedUser);
    // 6. Map user to response
    return userMapper.toUserResponse(savedUser);
  }

  /**
   * Updates a user
   * 
   * @param request the user request DTO
   * @return the user response DTO
   */
  @Override
  public UserResponse updateUser(UserRequest request) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
  }

  /**
   * Updates a user's status
   * 
   * @param id     the ID of the user
   * @param status the status of the user
   * @return the user response DTO
   */
  @Override
  public UserResponse updateUserStatus(Long id, UserStatus status) {
    User user = userRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("User", "id", id));
    if (user.getStatus() == status) {
      return userMapper.toUserResponse(user);
    }
    user.setStatus(status);
    return userMapper.toUserResponse(userRepo.save(user));
  }

  /**
   * Updates a user's password
   * 
   * @param id          the ID of the user
   * @param newPassword the new password
   */
  @Override
  public void updateUserPassword(Long id, String newPassword) {
    User user = userRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("User", "id", id));
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepo.save(user);
  }

  /**
   * Deletes a user
   * 
   * @param id the ID of the user
   */
  @Override
  public void deleteUser(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }

  /**
   * Checks if an user exists with the given email
   * 
   * @param email the email of the user
   * @return true if the email exists, false otherwise
   */
  @Transactional(readOnly = true)
  @Override
  public boolean isEmailExists(String email) {
    return userRepo.existsByEmail(email);
  }

}
