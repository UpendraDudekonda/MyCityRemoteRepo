package com.mycity.email.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/email")
public class MerchantEmailController {

    @Autowired
    private EmailService merchantEmailService;

    // Endpoint to generate OTP
    @PostMapping("/merchant/generateotp")
    public ResponseEntity<String> generateOTP(@Valid @RequestBody RequestOtpDTO request) {
        merchantEmailService.generateAndSendOTP(request.getEmail());
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    
}
