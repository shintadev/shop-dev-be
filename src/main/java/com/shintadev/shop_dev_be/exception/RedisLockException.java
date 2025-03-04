package com.shintadev.shop_dev_be.exception;

/**
 * Exception thrown when a Redis lock operation fails
 */
public class RedisLockException extends RuntimeException {

  /**
   * Create a new RedisLockException with a message
   * 
   * @param message the error message
   */
  public RedisLockException(String message) {
    super(message);
  }

  /**
   * Create a new RedisLockException with a message and cause
   * 
   * @param message the error message
   * @param cause   the cause of the exception
   */
  public RedisLockException(String message, Throwable cause) {
    super(message, cause);
  }
}