package com.shintadev.shop_dev_be.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shintadev.shop_dev_be.domain.dto.request.product.CategoryRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.CategoryResponse;

public interface CategoryService {
  List<CategoryResponse> getActiveCategories();

  List<CategoryResponse> getRootCategories();

  List<CategoryResponse> getSubcategories(Long id);

  List<CategoryResponse> searchCategories(String keyword);

  // ==================== Admin Methods ====================

  Page<CategoryResponse> getAllCategories(Pageable pageable);

  CategoryResponse getCategoryById(Long id);

  CategoryResponse createCategory(CategoryRequest request);

  CategoryResponse updateCategory(Long id, CategoryRequest request);

  void deleteCategory(Long id);
}
