package com.shintadev.shop_dev_be.domain.dto.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.request.cart.CartItemRequest;
import com.shintadev.shop_dev_be.domain.dto.response.cart.CartItemResponse;
import com.shintadev.shop_dev_be.domain.dto.response.cart.CartResponse;
import com.shintadev.shop_dev_be.domain.model.entity.cart.Cart;
import com.shintadev.shop_dev_be.domain.model.entity.cart.CartItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface CartMapper {

  CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "itemCount", expression = "java(cart.getItems().size())")
  CartResponse toCartResponse(Cart cart);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "cart", ignore = true)
  @Mapping(target = "product", ignore = true)
  CartItem toCartItem(CartItemRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "cart", ignore = true)
  @Mapping(target = "product", ignore = true)
  CartItem updateCartItemFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);

  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "productId", source = "cartItem.product.id")
  @Mapping(target = "productName", source = "cartItem.product.name")
  @Mapping(target = "productSlug", source = "cartItem.product.slug")
  @Mapping(target = "productImages", source = "cartItem.product.images")
  @Mapping(target = "price", source = "cartItem.product.price")
  @Mapping(target = "discountPrice", source = "cartItem.product.discountPrice")
  @Mapping(target = "subTotal", expression = "java(calculateSubTotal(cartItem))")
  CartItemResponse toCartItemResponse(CartItem cartItem);

  default BigDecimal calculateSubTotal(CartItem cartItem) {
    var itemPrice = cartItem.getProduct().getDiscountPrice() != null
        ? cartItem.getProduct().getDiscountPrice()
        : cartItem.getProduct().getPrice();

    return itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
  }
}
