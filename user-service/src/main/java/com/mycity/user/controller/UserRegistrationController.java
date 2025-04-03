package com.mycity.user.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.dto.*;
import com.mycity.user.service.UserServiceInterface;


//- For authentication-related endpoints.
//- `login()` : User login.
//- `logout()` : User logout.
//- `register()` : User registration.
//- `forgotPassword()` : Handles password reset requests.
//- `verifyAccount()` : handles account verification.

@RestController
@RequestMapping("/auth/user")
public class UserRegistrationController {

	
	@Autowired
	private UserServiceInterface userService;
	
	 @PostMapping("/register")
	 public ResponseEntity<String> registerUser(@RequestBody UserRegRequest request) {
	
	 try{
		 userService.registerUser(request);
	  return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
	  }
	 catch (IllegalArgumentException e) { 
	 return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	 }
	 
	 }
	 
}
