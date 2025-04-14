package com.mycity.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycity.admin.entity.Admin;
import com.mycity.admin.service.AdminAuthInterface;
import com.mycity.shared.admindto.AdminDetailsResponse;
import com.mycity.shared.admindto.AdminLoginRequest;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

	@Autowired
	private AdminAuthInterface adminAuthService;
	
	@PostMapping("/auth/internal/login")
	public ResponseEntity<AdminDetailsResponse> validateAdmin(@RequestBody AdminLoginRequest request) {
	   
	    Admin admin = adminAuthService.loginUser(request.getEmail(), request.getPassword());
	    if (admin != null) {
	        AdminDetailsResponse response = new AdminDetailsResponse(admin.getId(),admin.getEmail(), admin.getRole());
	        return ResponseEntity.ok(response);
	    }
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
