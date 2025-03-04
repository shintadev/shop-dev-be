package com.shintadev.shop_dev_be.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.user.Address;

/**
 * Repository for managing addresses
 */
@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

}
