package com.mycity.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.merchant.dto.MerchantRegRequest;
import com.mycity.merchant.service.MerchantServiceInterface;

@RestController
@RequestMapping("/auth/merchant")
public class MerchantRegController {
	
	@Autowired
	private MerchantServiceInterface merchantService;
	
	@PostMapping("/register")
    public ResponseEntity<String> registerMerchant(@RequestBody MerchantRegRequest request) {
        String response = merchantService.registerMerchant(request);
        return ResponseEntity.ok(response);
    }
	
	
	
}
