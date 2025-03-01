package com.shintadev.shop_dev_be.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shintadev.shop_dev_be.domain.model.entity.product.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

}
