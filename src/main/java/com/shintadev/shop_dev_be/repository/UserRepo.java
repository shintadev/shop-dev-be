package com.shintadev.shop_dev_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;

public interface UserRepo extends JpaRepository<User, Long> {

}
