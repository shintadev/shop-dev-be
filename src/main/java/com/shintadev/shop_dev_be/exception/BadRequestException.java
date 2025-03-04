package com.shintadev.shop_dev_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request is invalid
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  /**
   * Create a new BadRequestException with a message
   *
   * @param message the error message
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * Create a new BadRequestException with a message and cause
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

}
