package com.shintadev.shop_dev_be.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shintadev.shop_dev_be.domain.dto.request.product.ProductSearchCriteria;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.ProductResponse;

public interface ProductService {

  Page<ProductResponse> getActiveProducts(Pageable pageable);

  List<ProductResponse> getFeaturedProducts(int limit);

  ProductResponse getProductBySlug(String slug);

  Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

  List<ProductResponse> getRelatedProducts(Long id, int limit);

  Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

  Page<ProductResponse> filterProducts(ProductSearchCriteria searchCriteria, Pageable pageable);

  // ==================== Admin Methods ====================

  Page<ProductResponse> getAllProducts(Pageable pageable);

  ProductResponse createProduct(ProductRequest productRequest);

  ProductResponse updateProduct(Long id, ProductRequest productRequest);

  void deleteProduct(Long id);

  void refreshProductCache(String slug);

  void clearProductCaches();
}
