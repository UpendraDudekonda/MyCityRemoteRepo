package com.mycity.user.exception;

public class PlaceServiceUnavailableException extends RuntimeException {
    public PlaceServiceUnavailableException(String message) {
        super(message);
    }
}
