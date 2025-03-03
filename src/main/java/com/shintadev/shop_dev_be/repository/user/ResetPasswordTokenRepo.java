package com.shintadev.shop_dev_be.repository.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.user.ResetPasswordToken;

@Repository
public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, UUID> {

  Optional<List<ResetPasswordToken>> findByUserId(Long userId);
}