package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.request.user.AddressRequest;
import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.service.user.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * AddressController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-04
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

  private final AddressService addressService;

  /**
   * Get all addresses of the authenticated user
   * 
   * @param user the authenticated user
   * @return the list of addresses
   */
  @GetMapping
  public ResponseEntity<ApiResponse> getAllUserAddresses(@AuthenticationPrincipal User user) {
    var addresses = addressService.getAllUserAddresses(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Addresses fetched successfully")
            .data(addresses)
            .build());
  }

  /**
   * Get an address by its ID
   * 
   * @param id the ID of the address
   * @return the address
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getAddressById(@PathVariable Long id) {
    var address = addressService.getAddressById(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Address fetched successfully")
            .data(address)
            .build());
  }

  /**
   * Get the default address of the authenticated user
   * 
   * @param user the authenticated user
   * @return the default address
   */
  @GetMapping("/default")
  public ResponseEntity<ApiResponse> getDefaultAddress(@AuthenticationPrincipal User user) {
    var address = addressService.getDefaultAddress(user.getId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Default address fetched successfully")
            .data(address)
            .build());
  }

  /**
   * Create a new address for the authenticated user
   * 
   * @param user    the authenticated user
   * @param request the address request
   * @return the created address
   */
  @PostMapping
  public ResponseEntity<ApiResponse> createAddress(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody AddressRequest request) {
    var address = addressService.createAddress(user.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .success(true)
            .message("Address created successfully")
            .data(address)
            .build());
  }

  /**
   * Update an address by its ID
   * 
   * @param user    the authenticated user
   * @param id      the ID of the address
   * @param request the address request
   * @return the updated address
   */
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateAddress(
      @AuthenticationPrincipal User user,
      @PathVariable Long id,
      @Valid @RequestBody AddressRequest request) {
    var address = addressService.updateAddress(user.getId(), id, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Address updated successfully")
            .data(address)
            .build());
  }

  /**
   * Set the default address for the authenticated user
   * 
   * @param user the authenticated user
   * @param id   the ID of the address
   * @return the updated address
   */
  @PutMapping("/{id}/default")
  public ResponseEntity<ApiResponse> setDefaultAddress(
      @AuthenticationPrincipal User user,
      @PathVariable Long id) {
    var address = addressService.setDefaultAddress(user.getId(), id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Default address set successfully")
            .data(address)
            .build());
  }

  /**
   * Delete an address by its ID
   * 
   * @param user the authenticated user
   * @param id   the ID of the address
   * @return the deleted address
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> deleteAddress(
      @AuthenticationPrincipal User user,
      @PathVariable Long id) {
    addressService.deleteAddress(user.getId(), id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.builder()
            .success(true)
            .message("Address deleted successfully")
            .build());
  }
}
