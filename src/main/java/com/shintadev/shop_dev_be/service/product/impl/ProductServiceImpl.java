package com.shintadev.shop_dev_be.service.product.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.slugify.Slugify;
import com.shintadev.shop_dev_be.domain.dto.mapper.ProductMapper;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductSearchCriteria;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.ProductResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Category;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.product.CategoryRepo;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.service.product.ProductService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing products
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepo productRepo;
  private final ProductMapper productMapper;
  private final CategoryRepo categoryRepo;
  private final Slugify slugify;

  /**
   * Get all active products
   * 
   * @param pageable the pageable object
   * @return the page of active products
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "activeProducts", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<ProductResponse> getActiveProducts(Pageable pageable) {
    log.info("Fetching all active products for page {} with size {}",
        pageable.getPageNumber(), pageable.getPageSize());
    return productRepo.findAllActiveProducts(pageable)
        .map(productMapper::toProductResponse);
  }

  /**
   * Get all featured products
   * 
   * @param limit the limit of products
   * @return the list of featured products
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "featuredProducts", key = "#limit")
  public List<ProductResponse> getFeaturedProducts(int limit) {
    log.info("Fetching featured products with limit {}", limit);

    return productRepo.findAllFeaturedProducts(limit)
        .stream()
        .map(productMapper::toProductResponse)
        .toList();
  }

  /**
   * Get product by slug
   * 
   * @param slug the slug of the product
   * @return the product
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "productDetails", key = "#slug", unless = "#result == null")
  public ProductResponse getProductBySlug(String slug) {
    log.info("Fetching product details for slug: {}", slug);
    return productRepo.findBySlugAndActive(slug)
        .map(productMapper::toProductResponse)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "slug", slug));
  }

  /**
   * Get products by category
   * 
   * @param categoryId the id of the category
   * @param pageable   the pageable object
   * @return the page of products by category
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "productsByCategory", key = "#categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
    log.info("Fetching products by category ID: {} for page {} with size {}", categoryId,
        pageable.getPageNumber(), pageable.getPageSize());

    Category category = categoryRepo.findById(categoryId)
        .orElseThrow(() -> ResourceNotFoundException.create("Category", "id", categoryId));

    return productRepo.findByCategoryAndActive(category.getId(), pageable)
        .map(productMapper::toProductResponse);
  }

  /**
   * Get related products
   * 
   * @param id    the id of the product
   * @param limit the limit of products
   * @return the list of related products
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "relatedProducts", key = "#id + '_' + #limit", unless = "#result == null")
  public List<ProductResponse> getRelatedProducts(Long id, int limit) {
    log.info("Fetching related products for ID: {} with limit {}", id, limit);
    // 1. Get product by ID
    Product product = productRepo.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", id));

    // 2. Get related products by category
    List<Product> relatedProducts = productRepo.findRelatedProducts(
        product.getCategory().getId(),
        product.getId(),
        Pageable.ofSize(limit));

    // 3. If not enough related products, get featured products to fill the limit
    if (relatedProducts.isEmpty() || relatedProducts.size() < limit) {
      relatedProducts.addAll(productRepo.findByFeaturedTrueAndIdNotAndActive(
          product.getId(),
          Pageable.ofSize(limit - relatedProducts.size())));
    }

    return relatedProducts.stream()
        .map(productMapper::toProductResponse)
        .toList();
  }

  /**
   * Search products by keyword
   * 
   * @param keyword  the keyword to search for
   * @param pageable the pageable object
   * @return the page of products by keyword
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
    log.info("Searching for products with keyword {} on page {}", keyword, pageable.getPageNumber());
    return productRepo.findByKeywordContainingIgnoreCaseAndActive(keyword, pageable)
        .map(productMapper::toProductResponse);
  }

  /**
   * Filter products by criteria
   * 
   * @param searchCriteria the search criteria
   * @param pageable       the pageable object
   * @return the page of products by criteria
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponse> filterProducts(ProductSearchCriteria searchCriteria, Pageable pageable) {
    log.info("Filtering products with criteria {} on page {}", searchCriteria, pageable.getPageNumber());

    // 1. Build the specification
    Specification<Product> spec = buildSpecification(searchCriteria);

    // 2. Execute the query
    return productRepo.findAll(spec, pageable)
        .map(productMapper::toProductResponse);
  }

  // ==================== Admin Methods ====================

  /**
   * Get all products
   * 
   * @param pageable the pageable object
   * @return the page of products
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<ProductResponse> getAllProducts(Pageable pageable) {
    log.info("Fetching all products for page {} with size {}", pageable.getPageNumber(),
        pageable.getPageSize());
    return productRepo.findAll(pageable).map(productMapper::toProductResponse);
  }

  /**
   * Create a new product
   * 
   * @param productRequest the product request
   * @return the created product
   */
  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "activeProducts", allEntries = true),
      @CacheEvict(value = "featuredProducts", condition = "#productRequest.featured"),
      @CacheEvict(value = "productsByCategory", allEntries = true),
  })
  public ProductResponse createProduct(ProductRequest productRequest) {
    log.info("Creating new product: {}", productRequest);
    // 1. Check if the category exists
    Category category = categoryRepo.findById(productRequest.getCategoryId())
        .orElseThrow(() -> ResourceNotFoundException.create("Category", "id",
            productRequest.getCategoryId()));

    // 2. Create the product
    Product product = productMapper.toProduct(productRequest);
    product.setSlug(slugify.slugify(product.getName()));
    product.setCategory(category);

    // 3. Save the product
    product = productRepo.save(product);

    // 4. Refresh the product cache
    refreshProductCache(product.getSlug());

    return productMapper.toProductResponse(product);
  }

  /**
   * Update a product
   * 
   * @param id             the id of the product
   * @param productRequest the product request
   * @return the updated product
   */
  @Override
  @Caching(put = {
      @CachePut(value = "productDetails", key = "#result.slug")
  }, evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "activeProducts", allEntries = true),
      @CacheEvict(value = "featuredProducts", condition = "#productRequest.featured"),
      @CacheEvict(value = "productsByCategory", allEntries = true),
  })
  public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
    log.info("Updating product with ID {} to {}", id, productRequest);
    // 1. Check if the product exists
    Product product = productRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", id));

    // 2. Update the product
    productMapper.updateProductFromRequest(productRequest, product);

    // 3. Update the slug if the name has changed
    if (productRequest.getName() != null) {
      product.setSlug(slugify.slugify(productRequest.getName()));
    }

    // 4. Update the category if it exists and is different from the current
    // category
    if (productRequest.getCategoryId() != null
        && !productRequest.getCategoryId().equals(product.getCategory().getId())) {
      Category category = categoryRepo.findById(productRequest.getCategoryId())
          .orElseThrow(() -> ResourceNotFoundException.create("Category", "id",
              productRequest.getCategoryId()));
      product.setCategory(category);
    }

    product = productRepo.save(product);
    log.info("Updated product: {}", product);

    return productMapper.toProductResponse(product);
  }

  /**
   * Delete a product
   * 
   * @param id the id of the product
   */
  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "activeProducts", allEntries = true),
      @CacheEvict(value = "featuredProducts", allEntries = true),
      @CacheEvict(value = "productsByCategory", allEntries = true),
      @CacheEvict(value = "productDetails", key = "#productRepo.findById(id).get().slug"),
      @CacheEvict(value = "relatedProducts", allEntries = true),
  })
  public void deleteProduct(Long id) {
    log.info("Deleting product with ID: {}", id);
    // 1. Check if the product exists
    Product product = productRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", id));

    // 2. Set the status to deleted
    product.setStatus(ProductStatus.DELETED);

    // 3. Save the product
    productRepo.save(product);
  }

  /**
   * Refresh the product cache
   * 
   * @param slug the slug of the product
   */
  @Override
  public void refreshProductCache(String slug) {
    log.info("Refreshing product cache for slug: {}", slug);
    getProductBySlug(slug);
  }

  /**
   * Clear the product caches
   */
  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "activeProducts", allEntries = true),
      @CacheEvict(value = "featuredProducts", allEntries = true),
      @CacheEvict(value = "productsByCategory", allEntries = true),
      @CacheEvict(value = "productDetails", allEntries = true),
      @CacheEvict(value = "relatedProducts", allEntries = true),
  })
  public void clearProductCaches() {
    log.info("Clearing product caches");
  }

  /**
   * Build the specification for filtering products
   * 
   * @param searchCriteria the search criteria
   * @return the specification
   */
  private Specification<Product> buildSpecification(ProductSearchCriteria searchCriteria) {
    return (root, query, criteriaBuilder) -> {
      Specification<Product> spec = Specification.where(null);

      // Active products only
      spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), ProductStatus.ACTIVE));

      // Filter by keyword
      if (StringUtils.hasText(searchCriteria.getKeyword())) {
        spec = spec.and((r, q, cb) -> cb.or(
            cb.like(cb.lower(r.get("name")), "%" + searchCriteria.getKeyword().toLowerCase() + "%"),
            cb.like(cb.lower(r.get("description")), "%" + searchCriteria.getKeyword().toLowerCase() + "%")));
      }

      // Filter by category
      if (searchCriteria.getCategoryId() != null) {
        spec = spec.and((r, q, cb) -> {
          Join<Product, Category> categoryJoin = r.join("category", JoinType.INNER);
          return cb.equal(categoryJoin.get("id"), searchCriteria.getCategoryId());
        });
      }

      // Filter by price range
      if (searchCriteria.getMinPrice() != null) {
        spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("price"), searchCriteria.getMinPrice()));
      }

      if (searchCriteria.getMaxPrice() != null) {
        spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("price"), searchCriteria.getMaxPrice()));
      }

      // Filter by stock status
      if (Boolean.TRUE.equals(searchCriteria.getInStock())) {
        spec = spec.and((r, q, cb) -> cb.greaterThan(r.get("stock"), 0));
      }

      // Filter by on sale
      if (Boolean.TRUE.equals(searchCriteria.getOnSale())) {
        spec = spec.and((r, q, cb) -> cb.and(
            cb.greaterThan(r.get("discountPrice"), BigDecimal.ZERO),
            cb.greaterThan(r.get("discountPrice"), r.get("price"))));
      }

      return spec.toPredicate(root, query, criteriaBuilder);
    };
  }
}
