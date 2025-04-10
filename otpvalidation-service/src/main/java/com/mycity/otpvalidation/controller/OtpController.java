package com.mycity.otpvalidation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.otpvalidation.service.OtpService;
import com.mycity.shared.emaildto.VerifyOtpDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    // Endpoint to verify OTP
    @PostMapping("/verify-otp/user")
    public ResponseEntity<String> verifyOtp(@RequestBody  VerifyOtpDTO verificationRequest) {
        String email = verificationRequest.getEmail();
        String otp = verificationRequest.getOtp();

        boolean isValid = otpService.verifyOtp(email, otp);  // Verify OTP using OtpService
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }
}