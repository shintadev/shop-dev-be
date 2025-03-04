package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.service.product.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * CategoryController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-02
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Get all active categories
   * 
   * @return the list of active categories
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getActiveCategories() {
    var categories = categoryService.getActiveCategories();
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Categories fetched successfully", categories));
  }

  /**
   * Get all root categories
   * 
   * @return the list of root categories
   */
  @GetMapping("/root")
  public ResponseEntity<ApiResponse> getRootCategories() {
    var categories = categoryService.getRootCategories();
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Root categories fetched successfully", categories));
  }

  /**
   * Get all subcategories of a category
   * 
   * @param id the id of the category
   * @return the list of subcategories
   */
  @GetMapping("/{id}/subcategories")
  public ResponseEntity<ApiResponse> getSubcategories(@PathVariable Long id) {
    var subcategories = categoryService.getSubcategories(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Subcategories fetched successfully", subcategories));
  }

  /**
   * Search categories by keyword
   * 
   * @param keyword the keyword to search for
   * @return the list of categories
   */
  @GetMapping("/search")
  public ResponseEntity<ApiResponse> searchCategories(@RequestParam String keyword) {
    var categories = categoryService.searchCategories(keyword);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Categories fetched successfully", categories));
  }
}
