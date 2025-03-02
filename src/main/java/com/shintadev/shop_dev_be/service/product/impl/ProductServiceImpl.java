package com.shintadev.shop_dev_be.service.product.impl;

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

import com.github.slugify.Slugify;
import com.shintadev.shop_dev_be.domain.dto.mapper.ProductMapper;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductFilterRequest;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.ProductResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;
import com.shintadev.shop_dev_be.domain.model.enums.product.ProductStatus;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.product.ProductRepo;
import com.shintadev.shop_dev_be.service.product.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepo productRepo;

  private final ProductMapper productMapper;

  private final Slugify slugify;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "products", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<ProductResponse> getAllProducts(Pageable pageable) {
    log.info("Fetching all products for page {} with size {}",
        pageable.getPageNumber(), pageable.getPageSize());
    return productRepo.findAllActiveProducts(pageable)
        .map(productMapper::toProductResponse);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "productDetails", key = "#slug", unless = "#result == null")
  public ProductResponse getProductBySlug(String slug) {
    log.info("Fetching product details for slug: {}", slug);
    return productRepo.findBySlugAndActive(slug)
        .map(productMapper::toProductResponse)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "slug", slug));
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "relatedProducts", key = "#slug + '_' + #limit", unless = "#result == null")
  public List<ProductResponse> getRelatedProducts(String slug, int limit) {
    log.info("Fetching related products for slug {} with limit {}", slug, limit);

    Product product = productRepo.findBySlugAndActive(slug)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "slug", slug));

    List<Product> relatedProducts;
    if (product.getCategory() != null) {
      relatedProducts = productRepo.findByCategoryAndIdNotAndActive(
          product.getCategory().getId(),
          product.getId(),
          Pageable.ofSize(limit));
    } else {
      relatedProducts = productRepo.findByFeaturedTrueAndIdNotAndActive(
          product.getId(),
          Pageable.ofSize(limit));
    }

    return relatedProducts.stream()
        .map(productMapper::toProductResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "searchResults", key = "'search_' + #keyword + '_page_' + #pageable.pageNumber", unless = "#result.totalElements == 0")
  public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
    log.info("Searching for products with keyword {} on page {}", keyword, pageable.getPageNumber());
    return productRepo.findByKeywordContainingIgnoreCaseAndActive(keyword, pageable)
        .map(productMapper::toProductResponse);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "filteredResults", key = "'filter_' + #filterRequest.hashCode() + '_page_' + #pageable.pageNumber", unless = "#result.totalElements == 0")
  public Page<ProductResponse> filterProducts(ProductFilterRequest filterRequest, Pageable pageable) {
    log.info("Filtering products with criteria {} on page {}", filterRequest, pageable.getPageNumber());

    Specification<Product> spec = Specification.where(null);

    if (filterRequest.getCategoryId() != null) {
      spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"),
          filterRequest.getCategoryId()));
    }

    if (filterRequest.getMinPrice() != null) {
      spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
          filterRequest.getMinPrice()));
    }

    if (filterRequest.getMaxPrice() != null) {
      spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"),
          filterRequest.getMaxPrice()));
    }

    spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), "ACTIVE"));

    return productRepo.findAll(spec, pageable)
        .map(productMapper::toProductResponse);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "categories", allEntries = true)
  })
  public ProductResponse createProduct(ProductRequest productRequest) {
    log.info("Creating new product: {}", productRequest);

    Product product = productMapper.toProduct(productRequest);
    product.setSlug(slugify.slugify(product.getName()));

    product = productRepo.save(product);

    return productMapper.toProductResponse(product);
  }

  @Override
  @Caching(put = {
      @CachePut(value = "productDetails", key = "#result.slug")
  }, evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "relatedProducts", allEntries = true),
      @CacheEvict(value = "searchResults", allEntries = true),
      @CacheEvict(value = "filteredResults", allEntries = true)
  })
  public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
    log.info("Updating product with ID {} to {}", id, productRequest);

    Product product = productRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", id));

    productMapper.updateProductFromRequest(productRequest, product);

    if (productRequest.getName() != null) {
      log.info("Updating slug for product: {}", productRequest);
      product.setSlug(slugify.slugify(productRequest.getName()));
    }

    Product updatedProduct = productRepo.save(product);
    log.info("Updated product: {}", updatedProduct);

    return productMapper.toProductResponse(updatedProduct);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "productDetails", key = "#productRepo.findById(id).get().slug"),
      @CacheEvict(value = "relatedProducts", allEntries = true),
      @CacheEvict(value = "searchResults", allEntries = true),
      @CacheEvict(value = "filteredResults", allEntries = true)
  })
  public void deleteProduct(Long id) {
    log.info("Deleting product with ID: {}", id);

    Product product = productRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", id));

    product.setStatus(ProductStatus.DELETED);

    productRepo.save(product);
  }

  @Override
  public void refreshProductCache(Long productId) {
    log.info("Refreshing product cache for ID: {}", productId);

    Product product = productRepo.findByIdForUpdate(productId)
        .orElseThrow(() -> ResourceNotFoundException.create("Product", "id", productId));

    getProductBySlug(product.getSlug());
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "productDetails", allEntries = true),
      @CacheEvict(value = "relatedProducts", allEntries = true),
      @CacheEvict(value = "searchResults", allEntries = true),
      @CacheEvict(value = "filteredResults", allEntries = true),
      @CacheEvict(value = "categories", allEntries = true)
  })
  public void clearProductCaches() {
    log.info("Clearing product caches");
  }

}
