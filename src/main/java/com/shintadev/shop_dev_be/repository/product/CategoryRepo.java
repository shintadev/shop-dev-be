package com.shintadev.shop_dev_be.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shintadev.shop_dev_be.domain.model.entity.product.Category;

import jakarta.persistence.LockModeType;

/**
 * Repository for managing categories
 */
@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Category c WHERE c.id = :id")
  Optional<Category> findByIdForUpdate(Long id);

  @Query("SELECT c FROM Category c WHERE c.active = true")
  List<Category> findAllByActiveTrue();

  @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
  List<Category> findAllByParentIsNull();

  @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true")
  List<Category> findAllByParentId(Long parentId);

  @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
  List<Category> findAllActiveRootCategories();

  @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword% ")
  List<Category> searchCategories(String keyword);
}
