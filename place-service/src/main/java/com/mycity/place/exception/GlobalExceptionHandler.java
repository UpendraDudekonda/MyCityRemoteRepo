package com.mycity.place.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	    @ExceptionHandler(TooManyPlacesException.class)
	    public ResponseEntity<String> handleTooManyPlaces(TooManyPlacesException ex) 
	    {
	        return new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
	    }
	    
	    
	    @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	    }
	    

	    
	    @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage());
	    }
	    
	    @ExceptionHandler(EntityNotFoundException.class)
	    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(PlaceDataFetchException.class)
	    public ResponseEntity<Map<String, Object>> handlePlaceDataFetch(PlaceDataFetchException ex) {
	        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch place data.");
	    }


	    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
	        Map<String, Object> error = new HashMap<>();
	        error.put("timestamp", LocalDateTime.now());
	        error.put("status", status.value());
	        error.put("error", status.getReasonPhrase());
	        error.put("message", message);
	        return new ResponseEntity<>(error, status);
	    }
	    
	    @ExceptionHandler(PlaceNotFoundException.class)
	    public ResponseEntity<String> handlePlaceNotFound(PlaceNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(ex.getMessage()); // just the message
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
}
