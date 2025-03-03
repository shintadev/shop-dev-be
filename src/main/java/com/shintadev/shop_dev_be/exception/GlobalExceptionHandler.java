package com.shintadev.shop_dev_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.shintadev.shop_dev_be.domain.dto.response.ApiResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for consistent error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle ResourceNotFoundException
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .data(errorResponse)
            .build());
  }

  /**
   * Handle BadRequestException
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse> handleBadRequestException(
      BadRequestException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .data(errorResponse)
            .build());
  }

  /**
   * Handle AuthenticationException
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse> handleAuthenticationException(
      AuthenticationException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.UNAUTHORIZED.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .data(errorResponse)
            .build());
  }

  /**
   * Handle BadCredentialsException
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse> handleBadCredentialsException(
      BadCredentialsException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.UNAUTHORIZED.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.builder()
            .success(false)
            .message("Invalid credentials!")
            .data(errorResponse)
            .build());
  }

  /**
   * Handle AccessDeniedException
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.builder()
            .success(false)
            .message("Access denied!")
            .data(errorResponse)
            .build());
  }

  /**
   * Handle validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      WebRequest request) {

    Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage,
            (existingMessage, newMessage) -> existingMessage + ", " + newMessage));

    ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .fieldErrors(errors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.builder()
            .success(false)
            .message("Validation failed")
            .data(errorResponse)
            .build());
  }

  /**
   * Handle all other exceptions
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .path(request.getDescription(false))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .data(errorResponse)
            .build());
  }

  /**
   * Base error response class
   */
  @Data
  @SuperBuilder
  public static class ErrorResponse {
    private int status;
    private String path;
    private LocalDateTime timestamp;
  }

  /**
   * Error response for validation errors
   */
  @Data
  @SuperBuilder
  @EqualsAndHashCode(callSuper = true)
  public static class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;

    public Map<String, String> getFieldErrors() {
      return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
      this.fieldErrors = fieldErrors;
    }
  }
}