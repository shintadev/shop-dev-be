package com.shintadev.shop_dev_be.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shintadev.shop_dev_be.constant.ResourceName;
import com.shintadev.shop_dev_be.domain.dto.mapper.AddressMapper;
import com.shintadev.shop_dev_be.domain.dto.request.user.AddressRequest;
import com.shintadev.shop_dev_be.domain.dto.response.user.AddressResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.Address;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.exception.BadRequestException;
import com.shintadev.shop_dev_be.exception.ResourceNotFoundException;
import com.shintadev.shop_dev_be.repository.user.AddressRepo;
import com.shintadev.shop_dev_be.repository.user.UserRepo;
import com.shintadev.shop_dev_be.service.user.AddressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing addresses
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

  private final AddressRepo addressRepo;
  private final AddressMapper addressMapper;
  private final UserRepo userRepo;

  /**
   * Gets all addresses of a user
   * 
   * @param userId the ID of the user
   * @return the list of addresses
   */
  @Override
  @Transactional(readOnly = true)
  public List<AddressResponse> getAllUserAddresses(Long userId) {
    List<Address> addresses = addressRepo.findByUserId(userId);

    return addresses.stream()
        .map(addressMapper::toAddressResponse)
        .toList();
  }

  /**
   * Gets an address by its ID
   * 
   * @param id the ID of the address
   * @return the address
   */
  @Override
  @Transactional(readOnly = true)
  public AddressResponse getAddressById(Long id) {
    Address address = addressRepo.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ADDRESS, "id", id));

    return addressMapper.toAddressResponse(address);
  }

  /**
   * Gets the default address of a user
   * 
   * @param userId the ID of the user
   * @return the default address
   */
  @Override
  public AddressResponse getDefaultAddress(Long userId) {
    Address address = addressRepo.findByUserIdAndIsDefaultTrue(userId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ADDRESS, "userId", userId));

    return addressMapper.toAddressResponse(address);
  }

  /**
   * Creates an address for a user
   * 
   * @param userId  the ID of the user
   * @param request the address request
   * @return the created address
   */
  @Override
  public AddressResponse createAddress(Long userId, AddressRequest request) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.USER, "id", userId));

    Address address = addressMapper.toAddress(request);
    address.setUser(user);

    if (request.isDefault()) {
      unsetPreviousDefaultAddresses(userId);
    }

    if (user.getAddresses().isEmpty()) {
      address.setDefault(true);
    }

    if (user.getAddresses().size() >= 5) {
      throw new BadRequestException("You can only have up to 5 addresses");
    }

    address = addressRepo.save(address);

    return addressMapper.toAddressResponse(address);
  }

  /**
   * Updates an address
   * 
   * @param userId  the ID of the user
   * @param id      the ID of the address
   * @param request the address request
   * @return the updated address
   */
  @Override
  public AddressResponse updateAddress(Long userId, Long id, AddressRequest request) {
    Address address = addressRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ADDRESS, "id", id));

    if (!address.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You are not allowed to update this address");
    }

    if (request.isDefault() && !address.isDefault()) {
      unsetPreviousDefaultAddresses(userId);
    }

    addressMapper.updateAddressFromRequest(request, address);
    address = addressRepo.save(address);

    return addressMapper.toAddressResponse(address);
  }

  /**
   * Sets a default address for a user
   * 
   * @param userId the ID of the user
   * @param id     the ID of the address
   * @return the updated address
   */
  @Override
  public AddressResponse setDefaultAddress(Long userId, Long id) {
    Address address = addressRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ADDRESS, "id", id));

    if (!address.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You are not allowed to set this address as default");
    }

    if (address.isDefault()) {
      return addressMapper.toAddressResponse(address);
    }

    unsetPreviousDefaultAddresses(userId);

    address.setDefault(true);
    address = addressRepo.save(address);

    return addressMapper.toAddressResponse(address);
  }

  /**
   * Deletes an address
   * 
   * @param userId the ID of the user
   * @param id     the ID of the address
   */
  @Override
  public void deleteAddress(Long userId, Long id) {
    Address address = addressRepo.findByIdForUpdate(id)
        .orElseThrow(() -> ResourceNotFoundException.create(ResourceName.ADDRESS, "id", id));

    if (!address.getUser().getId().equals(userId)) {
      throw new AccessDeniedException("You are not allowed to delete this address");
    }

    if (address.isDefault()) {
      Optional<Address> anotherAddress = addressRepo.findByUserIdForUpdate(userId).stream()
          .filter(a -> !a.getId().equals(id))
          .findFirst();

      anotherAddress.ifPresent(addr -> {
        addr.setDefault(true);
        addressRepo.save(addr);
      });
    }

    addressRepo.delete(address);
  }

  /**
   * Un-sets the previous default addresses of a user
   * 
   * @param userId the ID of the user
   */
  private void unsetPreviousDefaultAddresses(Long userId) {
    Optional<Address> previousDefaultAddress = addressRepo.findByUserIdForUpdate(userId).stream()
        .filter(Address::isDefault)
        .findFirst();

    previousDefaultAddress.ifPresent(address -> {
      address.setDefault(false);
      addressRepo.save(address);
    });
  }
}
