package com.mycity.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycity.admin.service.AdminServiceInterface;
import com.mycity.shared.admindto.AdminLoginRequest;

@Controller
@RequestMapping("/auth/admin")
public class AdminLoginController {

	@Autowired
	private AdminServiceInterface adminServiceInterface;
	
	@PostMapping("/login")
	public ResponseEntity<String> loginMerchant(@RequestBody AdminLoginRequest request) {
	    String token = adminServiceInterface.loginAdmin(request); // Get the JWT token from service
	    return ResponseEntity.ok(token); // Return the token in response
	}
}
