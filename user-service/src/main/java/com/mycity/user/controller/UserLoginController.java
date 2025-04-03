package com.mycity.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.user.dto.UserLoginRequest;
import com.mycity.user.service.UserServiceInterface;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
public class UserLoginController {
	
	@Autowired
	private UserServiceInterface userService;
	
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest request) {
	    String token = userService.LoginUser(request); // Get the JWT token from service
	    return ResponseEntity.ok(token); // Return the token in response
	}

	//login for merchant
	
}
