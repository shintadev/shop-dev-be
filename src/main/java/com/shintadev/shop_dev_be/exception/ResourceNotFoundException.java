package com.shintadev.shop_dev_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Create a new ResourceNotFoundException with a message
   *
   * @param message the error message
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   * Create a new ResourceNotFoundException with a message and cause
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Create a new ResourceNotFoundException for a resource with an ID
   *
   * @param resourceName the name of the resource
   * @param fieldName    the name of the field
   * @param fieldValue   the value of the field
   * @return a new ResourceNotFoundException
   */
  public static ResourceNotFoundException create(String resourceName, String fieldName, Object fieldValue) {
    return new ResourceNotFoundException(
        String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
  }
}