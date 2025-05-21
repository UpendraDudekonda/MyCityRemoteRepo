package com.mycity.client.exception;

import java.util.concurrent.ExecutionException;

/**
 * Custom exception to indicate that the Media Service is unavailable (e.g.,
 * returns 503).
 */
public class MediaServiceUnavailableException extends RuntimeException {
	
	public MediaServiceUnavailableException(String message) {
		super(message);
	}

	public MediaServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public MediaServiceUnavailableException(String string, InterruptedException e) {
		// TODO Auto-generated constructor stub
	}

	public MediaServiceUnavailableException(String string, ExecutionException e) {
		// TODO Auto-generated constructor stub
	}
}
