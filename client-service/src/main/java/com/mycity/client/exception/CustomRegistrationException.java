package com.mycity.client.exception;

import com.mycity.shared.errordto.ErrorResponse;

public class CustomRegistrationException extends RuntimeException {

    private final ErrorResponse error;

    public CustomRegistrationException(ErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }
}
