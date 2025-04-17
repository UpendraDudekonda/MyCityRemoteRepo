package com.mycity.merchant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.merchant.service.MerchantServiceInterface;
import com.mycity.shared.merchantdto.MerchantProfileResponse;

@RestController
@RequestMapping("/merchant")
public class MerchantProfileController {
	
	//System.out.println("merchant profile request got into controller for retriving");
 
    private final MerchantServiceInterface merchantProfile;
 
    public MerchantProfileController(MerchantServiceInterface merchantProfile) {
        this.merchantProfile = merchantProfile;
    }
 
    @GetMapping("/profile")
    public ResponseEntity<MerchantProfileResponse> getMerchantProfile(@RequestHeader("X-User-Id") String merchantId) {
    	
    	System.out.println("merchant profile request got into methods for retriving");
    	
        MerchantProfileResponse merchant = merchantProfile.getMerchantById(merchantId);
        return ResponseEntity.ok(merchant);
    }
 
}