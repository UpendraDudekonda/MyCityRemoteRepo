package com.mycity.email.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.responsedto.OTPResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Endpoint to send OTP via email
    @PostMapping("/auth/generateotp")
    public ResponseEntity<String> requestOtp(@RequestBody RequestOtpDTO  otpRequest) {
        String email = otpRequest.getEmail();
        try {
            emailService.generateAndSendOTP(email);  // Call the EmailService to generate and send OTP
            return ResponseEntity.ok("OTP sent to email successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }
    

    @PostMapping("/auth/verifyotp")
     public ResponseEntity<OTPResponse> verifyOtp(@RequestBody VerifyOtpDTO verificationRequest) {
         String email = verificationRequest.getEmail();
         String otp = verificationRequest.getOtp();

         System.err.println("otp verification request entered into the email-service");
         try {
             boolean isValid = emailService.verifyOTP(email, otp);  // Verify OTP using OtpService
             if (isValid) {
                 return ResponseEntity.ok(new OTPResponse("OTP verified successfully", true));
             } else {
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new OTPResponse("Invalid or expired OTP.", false));
             }
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(new OTPResponse("Error verifying OTP: " + e.getMessage(), false));
         }
     
     }
}