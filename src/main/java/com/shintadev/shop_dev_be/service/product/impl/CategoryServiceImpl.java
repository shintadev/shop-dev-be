package com.shintadev.shop_dev_be.service.product.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.slugify.Slugify;
import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.domain.dto.mapper.CategoryMapper;
import com.shintadev.shop_dev_be.domain.dto.request.product.CategoryRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.CategoryResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Category;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.product.CategoryRepo;
import com.shintadev.shop_dev_be.service.product.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing categories
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepo categoryRepo;
  private final CategoryMapper categoryMapper;
  private final Slugify slugify;

  /**
   * Get all active categories
   * 
   * @return the list of active categories
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "activeCategories")
  public List<CategoryResponse> getActiveCategories() {
    return categoryRepo.findAllByActiveTrue()
        .stream()
        .map(categoryMapper::toCategoryResponseWithoutChildren)
        .toList();
  }

  /**
   * Get all root categories
   * 
   * @return the list of root categories
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "rootCategories")
  public List<CategoryResponse> getRootCategories() {
    return categoryRepo.findAllByParentIsNull()
        .stream()
        .map(categoryMapper::toCategoryResponse)
        .toList();
  }

  /**
   * Get all subcategories of a category
   * 
   * @param id the id of the category
   * @return the list of subcategories
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "subcategories", key = "#id")
  public List<CategoryResponse> getSubcategories(Long id) {
    return categoryRepo.findAllByParentId(id)
        .stream()
        .map(categoryMapper::toCategoryResponse)
        .toList();
  }

  /**
   * Search categories by keyword
   * 
   * @param keyword the keyword to search for
   * @return the list of categories
   */
  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> searchCategories(String keyword) {
    return categoryRepo.searchCategories(keyword)
        .stream()
        .map(categoryMapper::toCategoryResponseWithoutChildren)
        .toList();
  }

  // ==================== Admin Methods ====================

  /**
   * Get all categories
   * 
   * @param pageable the pageable object
   * @return the page of categories
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CategoryResponse> getAllCategories(Pageable pageable) {
    return categoryRepo.findAll(pageable)
        .map(categoryMapper::toCategoryResponseWithoutChildren);
  }

  /**
   * Get a category by id
   * 
   * @param id the id of the category
   * @return the category
   */
  @Override
  @Transactional(readOnly = true)
  public CategoryResponse getCategoryById(Long id) {
    return categoryRepo.findById(id)
        .map(categoryMapper::toCategoryResponse)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.CATEGORY, "id", id));
  }

  /**
   * Create a new category
   * 
   * @param request the category request
   * @return the created category
   */
  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "activeCategories", allEntries = true),
      @CacheEvict(value = "rootCategories", allEntries = true),
      @CacheEvict(value = "subcategories", allEntries = true),
  })
  public CategoryResponse createCategory(CategoryRequest request) {
    log.info("Creating new category: {}", request);
    Category category = categoryMapper.toCategory(request);
    category.setSlug(slugify.slugify(category.getName()));
    category = categoryRepo.save(category);

    return categoryMapper.toCategoryResponse(category);
  }

  /**
   * Update a category
   * 
   * @param id      the id of the category
   * @param request the category request
   * @return the updated category
   */
  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "activeCategories", allEntries = true),
      @CacheEvict(value = "rootCategories", allEntries = true),
      @CacheEvict(value = "subcategories", allEntries = true),
  })
  public CategoryResponse updateCategory(Long id, CategoryRequest request) {
    log.info("Updating category: {}", id);
    // 1. Check if the category exists
    Category category = categoryRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.CATEGORY, "id", id));

    // 2. Update the category
    categoryMapper.updateCategoryFromRequest(request, category);

    // 3. Update the slug if the name has changed
    if (request.getName() != null) {
      category.setSlug(slugify.slugify(request.getName()));
    }

    category = categoryRepo.save(category);
    log.info("Updated category: {}", category);
    return categoryMapper.toCategoryResponse(category);
  }

  /**
   * Delete a category
   * 
   * @param id the id of the category
   */
  @Override
  @Transactional
  @Caching(evict = {
      @CacheEvict(value = "activeCategories", allEntries = true),
      @CacheEvict(value = "rootCategories", allEntries = true),
      @CacheEvict(value = "subcategories", allEntries = true),
  })
  public void deleteCategory(Long id) {
    log.info("Deleting category: {}", id);
    // 1. Check if the category exists
    Category category = categoryRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.CATEGORY, "id", id));

    // 2. Delete the category
    categoryRepo.delete(category);
  }
}