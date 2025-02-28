package com.shintadev.shop_dev_be.repository.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shintadev.shop_dev_be.domain.model.entity.user.ResetPasswordToken;

public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, UUID> {

  Optional<List<ResetPasswordToken>> findByUserId(Long userId);
}