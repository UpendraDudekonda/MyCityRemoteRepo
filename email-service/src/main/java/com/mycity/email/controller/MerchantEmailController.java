package com.mycity.email.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.email.service.MerchantEmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;

@RestController
@RequestMapping("/auth/merchant")
public class MerchantEmailController {

    @Autowired
    private MerchantEmailService merchantEmailService;

    // Endpoint to generate OTP
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOTP(@RequestBody RequestOtpDTO request) {
        merchantEmailService.generateAndSendOTP(request.getEmail());
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    // Endpoint to verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody VerifyOtpDTO request) {
        boolean isVerified = merchantEmailService.verifyOTP(request.getEmail(), request.getOtp());
        if (isVerified) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
