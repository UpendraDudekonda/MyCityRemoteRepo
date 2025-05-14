package com.mycity.otpvalidation.exception;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mycity.shared.responsedto.OTPResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	  @ExceptionHandler(InvalidOtpException.class)
	    public ResponseEntity<OTPResponse> handleInvalidOtp(InvalidOtpException ex) {
	        return ResponseEntity.badRequest()
	                .body(new OTPResponse(ex.getMessage(), false));
	    }

    @ExceptionHandler(ExpiredOtpException.class)
    public ResponseEntity<OTPResponse> handleExpiredOtp(ExpiredOtpException ex) {
        return ResponseEntity.badRequest()
                .body(new OTPResponse(ex.getMessage(), false));
    }
  
    @ExceptionHandler(Exception.class)
    public ResponseEntity<OTPResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OTPResponse("Error verifying OTP: " + ex.getMessage(), false));
    }

}
