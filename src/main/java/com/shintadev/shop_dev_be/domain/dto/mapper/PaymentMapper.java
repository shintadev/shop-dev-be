package com.shintadev.shop_dev_be.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.response.order.PaymentResponse;
import com.shintadev.shop_dev_be.domain.model.entity.order.Payment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface PaymentMapper {

  PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "orderNumber", source = "order.orderNumber")
  PaymentResponse toPaymentResponse(Payment payment);
}
