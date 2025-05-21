package com.mycity.client.exception;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String placeName) {
        super("Place not found: " + placeName);
    }
}
