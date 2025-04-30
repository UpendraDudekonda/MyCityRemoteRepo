package com.mycity.email.Exception;

public class ExpiredOtpException extends RuntimeException {

    public ExpiredOtpException(String message) {
        super(message);
    }
}
