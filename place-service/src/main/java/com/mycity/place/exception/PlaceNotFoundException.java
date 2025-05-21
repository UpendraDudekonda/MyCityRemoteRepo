package com.mycity.place.exception;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String placeName) {
        super("Place not found with name: " + placeName);
    }
}

