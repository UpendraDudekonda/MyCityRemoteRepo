package com.mycity.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.service.UserAuthenticationInterface;

@RestController
@RequestMapping("/user")
public class UserRegistrationController {

    @Autowired
    private UserAuthenticationInterface userService;

    @PostMapping("/auth/internal/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegRequest userRequest) {
        try {
            String message = userService.registerUser(userRequest);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage(), 500));
        }
    }
}
