package com.shintadev.shop_dev_be.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
  @GetMapping
  public String sayHello() {
    return "Hello, World!";
  }

  @GetMapping("/greet/{name}")
  public String sayHello(@PathVariable String name) {
    return "Hello, " + name + "!";
  }
}
