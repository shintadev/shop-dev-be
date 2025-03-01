package com.shintadev.shop_dev_be.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.shintadev.shop_dev_be.domain.model.entity.product.Product;

import jakarta.persistence.LockModeType;

public interface ProductRepo extends JpaRepository<Product, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Product p WHERE p.id = :id")
  Optional<Product> findByIdForUpdate(Long id);

  @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
  Page<Product> findAllActiveProducts(Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.slug = :slug AND p.status = 'ACTIVE'")
  Optional<Product> findBySlugAndActive(String slug);

  @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId AND p.status = 'ACTIVE'")
  List<Product> findByCategoryAndIdNotAndActive(Long categoryId, Long productId, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.featured = true AND p.id != :productId AND p.status = 'ACTIVE'")
  List<Product> findByFeaturedTrueAndIdNotAndActive(Long productId, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE " +
      "(lower(p.name) LIKE lower(concat('%', :keyword, '%')) OR " +
      "lower(p.description) LIKE lower(concat('%', :keyword, '%'))) AND " +
      "p.status = 'ACTIVE'")
  Page<Product> findByKeywordContainingIgnoreCaseAndActive(String keyword, Pageable pageable);

  Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
