package com.shintadev.shop_dev_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shintadev.shop_dev_be.domain.model.entity.user.User;
import com.shintadev.shop_dev_be.repository.user.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppController {
  private final UserRepo userRepo;

  @GetMapping
  public String sayHello() {
    return "Hello, World!";
  }

  @GetMapping("/greet/{name}")
  public String sayHello(@PathVariable String name) {
    return "Hello, " + name + "!";
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers(HttpServletRequest request) {
    System.out.println(request.getHeader("Host")); // localhost:8080
    return new ResponseEntity<>(userRepo.findAll(), null, HttpStatus.OK);
  }

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    return new ResponseEntity<>(userRepo.save(user), null, HttpStatus.CREATED);
  }

  @GetMapping("/profile")
  public ResponseEntity<User> getProfile() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
