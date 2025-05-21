package com.mycity.client.exception;

public class ReviewServiceUnavailableException extends RuntimeException {
	 public ReviewServiceUnavailableException(String message) {
	        super(message);
	    }
	    public ReviewServiceUnavailableException(String message, Throwable cause) {
	        super(message, cause);
	    }
}
