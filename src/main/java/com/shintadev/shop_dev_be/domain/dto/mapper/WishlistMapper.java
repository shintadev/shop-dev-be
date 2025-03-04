package com.shintadev.shop_dev_be.domain.dto.mapper;

import com.shintadev.shop_dev_be.domain.dto.response.wishlist.WishlistItemResponse;
import com.shintadev.shop_dev_be.domain.dto.response.wishlist.WishlistResponse;
import com.shintadev.shop_dev_be.domain.model.entity.wishlist.Wishlist;
import com.shintadev.shop_dev_be.domain.model.entity.wishlist.WishlistItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface WishlistMapper {

  WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "itemCount", expression = "java(wishlist.getItems().size())")
  WishlistResponse toWishlistResponse(Wishlist wishlist);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "productSlug", source = "product.slug")
  @Mapping(target = "productImages", source = "product.images")
  @Mapping(target = "productPrice", source = "product.price")
  @Mapping(target = "productDiscountPrice", source = "product.discountPrice")
  @Mapping(target = "inStock", expression = "java(wishlistItem.getProduct().getStock() > 0)")
  WishlistItemResponse toWishlistItemResponse(WishlistItem wishlistItem);
}
