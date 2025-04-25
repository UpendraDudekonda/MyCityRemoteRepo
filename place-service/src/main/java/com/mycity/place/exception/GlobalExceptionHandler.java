package com.mycity.place.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	    @ExceptionHandler(TooManyPlacesException.class)
	    public ResponseEntity<String> handleTooManyPlaces(TooManyPlacesException ex) 
	    {
	        return new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
	    }
}
