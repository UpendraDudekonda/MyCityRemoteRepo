package com.mycity.review.exception;

public class PlaceServiceUnavailableException extends RuntimeException {
    public PlaceServiceUnavailableException(String message) {
        super(message);
    }
}
