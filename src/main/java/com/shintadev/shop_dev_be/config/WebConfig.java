package com.shintadev.shop_dev_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the application
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.cors.allowed-origins}")
  private String allowedOrigins;

  /**
   * Adds CORS mappings to the registry
   * 
   * @param registry the CORS registry
   */
  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(allowedOrigins.split(","))
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }

}
