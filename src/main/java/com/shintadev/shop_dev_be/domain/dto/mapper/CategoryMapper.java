package com.shintadev.shop_dev_be.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Qualifier;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.request.product.CategoryRequest;
import com.shintadev.shop_dev_be.domain.dto.response.product.CategoryResponse;
import com.shintadev.shop_dev_be.domain.model.entity.product.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface CategoryMapper {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "parent", ignore = true)
  @Mapping(target = "children", ignore = true)
  Category toCategory(CategoryRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "parent", ignore = true)
  @Mapping(target = "children", ignore = true)
  void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category category);

  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "parentName", source = "parent.name")
  @Mapping(target = "productCount", ignore = true)
  @Mapping(target = "children", qualifiedBy = WithoutChildren.class)
  CategoryResponse toCategoryResponse(Category category);

  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "parentName", source = "parent.name")
  @Mapping(target = "productCount", ignore = true)
  @Mapping(target = "children", ignore = true)
  @WithoutChildren
  CategoryResponse toCategoryResponseWithoutChildren(Category category);

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.CLASS)
  public @interface WithoutChildren {
  }
}
