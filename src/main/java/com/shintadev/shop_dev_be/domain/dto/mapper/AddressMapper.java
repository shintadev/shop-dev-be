package com.shintadev.shop_dev_be.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.shintadev.shop_dev_be.domain.dto.request.user.AddressRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.AddressResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.Address;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface AddressMapper {

  AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  Address toAddress(AddressRequest request);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "formattedAddress", expression = "java(formatAddress(address))")
  AddressResponse toAddressResponse(Address address);

  default String formatAddress(Address address) {
    return new StringBuilder()
        .append(address.getAddressLine1())
        .append(", ")
        .append(address.getAddressLine2())
        .append(", ")
        .append(address.getWard())
        .append(", ")
        .append(address.getDistrict())
        .append(", ")
        .append(address.getProvinceCity())
        .append(" ")
        .append(address.getPostalCode())
        .toString();
  }
}
