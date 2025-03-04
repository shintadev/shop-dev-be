package com.shintadev.shop_dev_be.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.product.ProductSearchCriteria;
import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.service.product.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * ProductController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-02
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  /**
   * Get all products
   * 
   * @param page      the page number
   * @param size      the number of products per page
   * @param sortBy    the field to sort by
   * @param sortOrder the order to sort by
   * @return the list of products by page
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getAllProducts(
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var products = productService.getActiveProducts(pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }

  /**
   * Get featured products
   * 
   * @return the list of featured products
   */
  @GetMapping("/featured")
  public ResponseEntity<ApiResponse> getFeaturedProducts(@RequestParam(defaultValue = "10") Integer limit) {
    var products = productService.getFeaturedProducts(limit);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }

  /**
   * Get product by slug
   * 
   * @param slug the slug of the product
   * @return the product
   */
  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable String slug) {
    var product = productService.getProductBySlug(slug);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Product fetched successfully", product));
  }

  /**
   * Get products by category id
   * 
   * @param id the id of the category
   * @return the list of products by category
   */
  @GetMapping("/categories/{id}")
  public ResponseEntity<ApiResponse> getProductsByCategory(
      @PathVariable Long id,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var products = productService.getProductsByCategory(id, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }

  /**
   * Get products related to the product by id
   * 
   * @param id    the id of the product
   * @param limit the limit of the related products
   * @return the list of related products
   */
  @GetMapping("/{id}/related")
  public ResponseEntity<ApiResponse> getRelatedProducts(@PathVariable Long id,
      @RequestParam(required = false) Integer limit) {
    var products = productService.getRelatedProducts(id, limit);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }

  /**
   * Search products
   * 
   * @param keyword the keyword to search for
   * @param page    the page number
   * @param size    the number of products per page
   * @return the list of products by keyword
   */
  @GetMapping("/search")
  public ResponseEntity<ApiResponse> searchProducts(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var products = productService.searchProducts(keyword, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }

  /**
   * Filter products
   * 
   * @param searchCriteria the search criteria
   * @param page           the page number
   * @param size           the number of products per page
   * @param sortBy         the field to sort by
   * @param sortOrder      the order to sort by
   * @return the list of products by search criteria
   */
  @GetMapping("/filter")
  public ResponseEntity<ApiResponse> filterProducts(
      @RequestBody ProductSearchCriteria searchCriteria,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    int pageNumber = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
    var products = productService.filterProducts(searchCriteria, pageable);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Products fetched successfully", products));
  }
}
