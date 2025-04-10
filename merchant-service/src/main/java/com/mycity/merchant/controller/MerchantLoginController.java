package com.mycity.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.merchant.service.MerchantServiceInterface;
import com.mycity.shared.merchantdto.MerchantLoginReq;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/merchant")
@RequiredArgsConstructor
public class MerchantLoginController {
	
	@Autowired
	private MerchantServiceInterface merchantServiceInterface;
	
	@PostMapping("/login")
	public ResponseEntity<String> loginMerchant(@RequestBody MerchantLoginReq request) {
	    String token = merchantServiceInterface.loginMerchant(request); // Get the JWT token from service
	    return ResponseEntity.ok(token); // Return the token in response
	}
	
}

