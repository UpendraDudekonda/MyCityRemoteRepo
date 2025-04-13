package com.mycity.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.responsedto.OTPResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthOtpController {

    private final WebClient.Builder webClientBuilder;

    private static final String EMAIL_SERVICE_URL = "lb://EMAIL-SERVICE";
    private static final String OTP_SERVICE_URL = "lb://OTP-SERVICE";
    
   

    //this is made up in the user service / direclty can be called using the email service 
    @PostMapping("/email/send")
    public ResponseEntity<?> sendOtp(@RequestBody RequestOtpDTO request) {
        try {
            String response = webClientBuilder.build()
                .post()
                .uri(EMAIL_SERVICE_URL + "/email/auth/generateotp")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse("Email service error: " + e.getResponseBodyAsString(), e.getStatusCode().value()));

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to send OTP: " + e.getMessage(), 500));
        }
    }
    
    @PostMapping("/otp/verifyotp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDTO request) {
        try {
            OTPResponse response = webClientBuilder.build()
                .post()
                .uri(OTP_SERVICE_URL + "/otp/auth/verifyotp")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OTPResponse.class)
                .block();  // Blocking call to get the response

            if (response.isOtpVerified()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("OTP service error: " + e.getResponseBodyAsString(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to verify OTP: " + e.getMessage(), 500));
        }
    }


    
    
    
    
}
