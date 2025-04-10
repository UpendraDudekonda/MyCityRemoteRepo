package com.mycity.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.service.UserServiceInterface;

@RestController
@RequestMapping("/auth/user")
public class UserRegistrationController {

    @Autowired
    private UserServiceInterface userService;

    // Endpoint to start the registration process (send OTP)
    @PostMapping("/start-registration")
    public ResponseEntity<String> startRegistration(@RequestBody UserRegRequest request) {
        try {
            // Validate request fields (e.g. validate if email, phone number, etc., are not null/empty)
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is required.");
            }

            // Call the service to initiate OTP sending
            userService.startRegistration(request);

            return new ResponseEntity<>("OTP sent successfully. Please verify it to complete registration.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Catching general exceptions
            return new ResponseEntity<>("An error occurred while starting the registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to complete the registration process (verify OTP and register)
    @PostMapping("/complete-registration")
    public ResponseEntity<String> completeRegistration(@RequestBody UserRegRequest request, @RequestParam String otp) {
        try {
            // Validate the OTP and the request object
            if (otp == null || otp.isEmpty()) {
                throw new IllegalArgumentException("OTP is required.");
            }

            // Validate other fields, if necessary
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is required.");
            }

            // Call the service to complete registration after OTP verification
            userService.completeRegistration(request, otp);

            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Catching general exceptions
            return new ResponseEntity<>("An error occurred while completing registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
