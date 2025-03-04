package com.shintadev.shop_dev_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when authentication fails
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {

  /**
   * Create a new AuthenticationException with a message
   *
   * @param message the error message
   */
  public AuthenticationException(String message) {
    super(message);
  }

  /**
   * Create a new AuthenticationException with a message and cause
   *
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
