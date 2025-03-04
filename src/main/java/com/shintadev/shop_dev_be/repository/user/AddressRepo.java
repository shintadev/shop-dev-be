package com.shintadev.shop_dev_be.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.user.Address;

import jakarta.persistence.LockModeType;

/**
 * Repository for managing addresses
 */
@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

  List<Address> findByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_READ)
  @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
  Optional<Address> findByUserIdForUpdate(Long userId);

  Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Address a WHERE a.id = :id")
  Optional<Address> findByIdForUpdate(Long id);

}
