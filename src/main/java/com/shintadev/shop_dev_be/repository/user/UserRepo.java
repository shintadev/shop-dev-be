package com.shintadev.shop_dev_be.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;

import jakarta.persistence.LockModeType;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = ?1")
  Optional<User> findByIdForUpdate(Long id);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
