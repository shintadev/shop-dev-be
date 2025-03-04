package com.shintadev.shop_dev_be.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.request.order.OrderRequest;
import com.shintadev.shop_dev_be.domain.dto.response.order.OrderItemResponse;
import com.shintadev.shop_dev_be.domain.dto.response.order.OrderResponse;
import com.shintadev.shop_dev_be.domain.model.entity.order.Order;
import com.shintadev.shop_dev_be.domain.model.entity.order.OrderItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface OrderMapper {

  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "subtotal", ignore = true)
  @Mapping(target = "shippingFee", ignore = true)
  @Mapping(target = "tax", ignore = true)
  @Mapping(target = "totalPrice", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "shippingAddress", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "payment", ignore = true)
  @Mapping(target = "orderAt", ignore = true)
  @Mapping(target = "paymentAt", ignore = true)
  @Mapping(target = "shippedAt", ignore = true)
  @Mapping(target = "deliveredAt", ignore = true)
  @Mapping(target = "cancelledAt", ignore = true)
  Order toOrder(OrderRequest request);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userName", expression = "java(order.getUser().getFirstName() + ' ' + order.getUser().getLastName())")
  OrderResponse toOrderResponse(Order order);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "productImage", expression = "java(orderItem.getProduct().getImages().isEmpty() ? null : orderItem.getProduct().getImages().get(0))")
  OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
