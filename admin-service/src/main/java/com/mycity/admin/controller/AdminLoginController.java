package com.mycity.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.mycity.admin.entity.Admin;

@Controller
public class AdminLoginController {

	
	
	
	public ResponseEntity<Admin> Login(@RequestBody Admin admin){
		return null;
		
	}

}
