package com.mycity.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.responsedto.LoginResponse;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.user.entity.User;
import com.mycity.user.service.UserAuthenticationInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserLoginController {

    private final UserAuthenticationInterface userService;

    @PostMapping("/auth/internal/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        // Call the service to perform the login
        User user = userService.loginUser(request.getEmail(), request.getPassword());

        if (user != null) {
            // Successful login
        	//You Can Use UserDetailsResponse Also if needed 
            LoginResponse response = new LoginResponse(true, "Login Successful",user.getId(), user.getEmail(), user.getRole());
            return ResponseEntity.ok(response);
        } else {
            //User Not Found Exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Invalid credentials", null,null, null));
        }
    }
}
