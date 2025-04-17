package com.mycity.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.service.UserAuthenticationInterface;

import lombok.RequiredArgsConstructor;


//Didn't Used this Code Yet
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserOtpRegistrationController {

    private final UserAuthenticationInterface userService;

    @PostMapping("/auth/startreg")
    public ResponseEntity<String> startRegistration(@RequestBody RequestOtpDTO request) {
        userService.startRegistration(request);
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    //Web Annotation
    @PostMapping("/auth/completereg")
    public ResponseEntity<String> completeRegistration(@RequestBody UserRegRequest request,
                                                       @RequestParam("otp") String otp) {
        userService.completeRegistration(request, otp);
        return ResponseEntity.ok("User registered successfully.");
    }
}
