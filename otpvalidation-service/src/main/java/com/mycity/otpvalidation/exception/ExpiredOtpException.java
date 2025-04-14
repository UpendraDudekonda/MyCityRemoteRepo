package com.mycity.otpvalidation.exception;


public class ExpiredOtpException extends RuntimeException {

    public ExpiredOtpException(String message) {
        super(message);
    }
}
