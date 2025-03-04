package com.shintadev.shop_dev_be.service.user;

import java.util.List;

import com.shintadev.shop_dev_be.domain.dto.request.user.AddressRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.AddressResponse;

public interface AddressService {

  List<AddressResponse> getAllUserAddresses(Long userId);

  AddressResponse getAddressById(Long id);

  AddressResponse getDefaultAddress(Long userId);

  AddressResponse createAddress(Long userId, AddressRequest request);

  AddressResponse updateAddress(Long userId, Long id, AddressRequest request);

  AddressResponse setDefaultAddress(Long userId, Long id);

  void deleteAddress(Long userId, Long id);
}
