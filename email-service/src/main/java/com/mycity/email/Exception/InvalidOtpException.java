package com.mycity.email.Exception;

public class InvalidOtpException extends RuntimeException {
    
    public InvalidOtpException(String message) {
        super(message);
    }
}