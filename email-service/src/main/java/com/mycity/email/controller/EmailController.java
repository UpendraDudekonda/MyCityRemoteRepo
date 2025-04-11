package com.mycity.email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Endpoint to send OTP via email
    @PostMapping("/request-otp/user")
    public ResponseEntity<String> requestOtp(@RequestBody RequestOtpDTO  otpRequest) {
        String email = otpRequest.getEmail();
        try {
            emailService.generateAndSendOtp(email);  // Call the EmailService to generate and send OTP
            return ResponseEntity.ok("OTP sent to email successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }
}