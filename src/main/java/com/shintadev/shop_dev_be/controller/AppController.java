package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;
import com.shintadev.shop_dev_be.domain.model.entity.user.User;

import jakarta.servlet.http.HttpServletRequest;

/**
 * AppController
 * 
 * @author Shintadev
 * @version 1.0
 * @since 2025-03-03
 */
@RestController
@RequestMapping("/app")
public class AppController {

  /**
   * Say hello world
   * 
   * @return the response entity
   */
  @GetMapping
  public ResponseEntity<ApiResponse> sayHello() {
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Hello, World!", null));
  }

  /**
   * Say hello to a specific name
   * 
   * @param name the name
   * @return the response entity
   */
  @GetMapping("/greet/{name}")
  public ResponseEntity<ApiResponse> sayHello(@PathVariable String name) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Hello, " + name + "!", null));
  }

  /**
   * Get the host
   * 
   * @param request the HttpServletRequest
   * @return the host
   */
  @GetMapping("/host")
  public ResponseEntity<ApiResponse> getHost(HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Host fetched successfully", request.getHeader("Host")));
  }

  /**
   * Get the profile // TODO: move to user controller
   * 
   * @return current user's profile
   */
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse> getProfile() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse(true, "Profile fetched successfully", user));
  }
}
