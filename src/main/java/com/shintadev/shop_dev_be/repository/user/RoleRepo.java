package com.shintadev.shop_dev_be.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.user.Role;
import com.shintadev.shop_dev_be.domain.model.enums.user.RoleName;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);

  Optional<Role> findByName(RoleName name);
}
