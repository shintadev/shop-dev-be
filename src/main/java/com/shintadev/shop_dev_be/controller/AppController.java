package com.shintadev.shop_dev_be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppController {

  @GetMapping
  public String sayHello() {
    return "Hello, World!";
  }

  @GetMapping("/greet/{name}")
  public String sayHello(@PathVariable String name) {
    return "Hello, " + name + "!";
  }

  @GetMapping("/host")
  public String getHost(HttpServletRequest request) {
    return request.getHeader("Host");
  }

  @GetMapping("/profile")
  public ResponseEntity<User> getProfile() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
