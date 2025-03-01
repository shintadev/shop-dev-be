package com.shintadev.shop_dev_be.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shintadev.shop_dev_be.domain.dto.request.product.ProductFilterRequest;
import com.shintadev.shop_dev_be.domain.dto.request.product.ProductRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.ProductResponse;

public interface ProductService {

  Page<ProductResponse> getAllProducts(Pageable pageable);

  ProductResponse getProductBySlug(String slug);

  List<ProductResponse> getRelatedProducts(String slug, int limit);

  Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

  Page<ProductResponse> filterProducts(ProductFilterRequest filterRequest, Pageable pageable);

  // Admin methods
  ProductResponse createProduct(ProductRequest productRequest);

  ProductResponse updateProduct(Long id, ProductRequest productRequest);

  void deleteProduct(Long id);

  void refreshProductCache(Long productId);

  void clearProductCaches();
}
