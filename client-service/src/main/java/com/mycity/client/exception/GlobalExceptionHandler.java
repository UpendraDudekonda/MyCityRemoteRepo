package com.mycity.client.exception;

import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mycity.shared.errordto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleCustomRegistrationException(CustomRegistrationException ex) {
        return new ResponseEntity<>(ex.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlaceNotFound(PlaceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of(
                "error", "Not Found",
                "status", 404,
                "message", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(ReviewServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleReviewServiceUnavailable(ReviewServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            Map.of(
                "error", "Review Service Unavailable",
                "status", 503,
                "message", ex.getMessage()
            )
        );
    }
    

    @ExceptionHandler(MediaServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleMediaServiceError(MediaServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            Map.of(
                "error", "Media Service Unavailable",
                "status", 503,
                "message", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(EventServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleEventServiceError(EventServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            Map.of(
                "error", "Event Service Unavailable",
                "status", 503,
                "message", ex.getMessage()
            )
        );
    }
 // General Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Something went wrong: " + e.getMessage());
    }

    // WebClient Response Exception Handler (client and server errors)
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
}
