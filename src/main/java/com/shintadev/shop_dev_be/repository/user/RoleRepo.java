package com.shintadev.shop_dev_be.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shintadev.shop_dev_be.domain.model.entity.user.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
