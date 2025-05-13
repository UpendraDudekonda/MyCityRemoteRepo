package com.mycity.otpvalidation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.otpvalidation.service.OtpService;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.responsedto.OTPResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class VerifyOtpController {

    private final OtpService otpService; 

    @PostMapping("/auth/verifyotp")
    public ResponseEntity<OTPResponse> verifyOtp(@RequestBody VerifyOtpDTO verificationRequest) {
        String email = verificationRequest.getEmail();
        String otp = verificationRequest.getOtp();

        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(new OTPResponse("OTP verified successfully", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(new OTPResponse("Invalid or expired OTP.", false));
        }
    }


}
