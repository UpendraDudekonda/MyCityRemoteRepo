package com.mycity.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Invalid Place exception (Client-side error)
    @ExceptionHandler(InvalidPlaceException.class)
    public ResponseEntity<String> handleInvalidPlace(InvalidPlaceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Handle Place Service Unavailable exception (Server-side error)
    @ExceptionHandler(PlaceServiceUnavailableException.class)
    public ResponseEntity<String> handlePlaceServiceDown(PlaceServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    // Handle Invalid User exception (Client-side error)
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<String> handleInvalidUser(InvalidUserException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // Handle User Service Unavailable exception (Server-side error)
    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<String> handleUserServiceDown(UserServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    // Handle Review Not Found exception (Client-side error)
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFound(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // Handle Media Service Unavailable exception (Server-side error)
    @ExceptionHandler(MediaServiceException.class)
    public ResponseEntity<String> handleMediaServiceDown(MediaServiceException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    // Handle 4xx Client Errors (for HTTP status codes in 400 range)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException e) {
        if (e.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(e.getStatusCode()).body("Client Error: " + e.getMessage());
        } else if (e.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(e.getStatusCode()).body("Server Error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
    }
    
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<String> handlePlaceNotFound(PlaceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Place is not present");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }
}
