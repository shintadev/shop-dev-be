package com.shintadev.shop_dev_be.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.request.product.ProductRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.ProductResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Product;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface ProductMapper {

  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "category", ignore = true)
  Product toProduct(ProductRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "category", ignore = true)
  void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);

  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "categorySlug", source = "category.slug")
  @Mapping(target = "inStock", expression = "java(product.getStock() > 0)")
  @Mapping(target = "onSale", expression = "java(product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(product.getPrice()) < 0)")
  ProductResponse toProductResponse(Product product);
}
