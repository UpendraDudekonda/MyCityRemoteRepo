package com.mycity.user.exception;

import java.util.HashMap;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mycity.shared.errordto.ErrorResponse;

import jakarta.ws.rs.ServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle UserNotFoundException (if any)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Handle IllegalArgumentException (e.g., validation errors or invalid credentials)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle any other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Unexpected server error", 500);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
 // Handle 4xx Client Errors
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException e) {
        if (e.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(e.getStatusCode())
                                 .body("Client Error: " + e.getMessage());
        } else if (e.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(e.getStatusCode())
                                 .body("Server Error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Unexpected Error: " + e.getMessage());
    }

    // Handle Review Not Found exception (404)
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFound(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Review not found: " + e.getMessage());
    }

    // Handle Invalid Review request (400)
    @ExceptionHandler(InvalidReviewException.class)
    public ResponseEntity<String> handleInvalidReview(InvalidReviewException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Invalid review request: " + e.getMessage());
    }

    // Handle Service Unavailable (503)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailable(ServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                             .body("Service Unavailable: " + e.getMessage());
    }
    // Handle Media Service Unavailable exception (Server-side error)
    @ExceptionHandler(MediaServiceException.class)
    public ResponseEntity<String> handleMediaServiceDown(MediaServiceException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }
    
 // Handle Place Service Unavailable exception (Server-side error)
    @ExceptionHandler(PlaceServiceUnavailableException.class)
    public ResponseEntity<String> handlePlaceServiceDown(PlaceServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }
}
