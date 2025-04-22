package com.mycity.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.merchant.entity.Merchant;
import com.mycity.merchant.service.MerchantAuthInterface;
import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.merchantdto.MerchantDetailsResponse;
import com.mycity.shared.merchantdto.MerchantLoginRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantLoginController {
	
	@Autowired
	private MerchantAuthInterface merchantAuthService;
	
	
	@PostMapping("/auth/internal/login")
	public ResponseEntity<?> loginMerchant(@RequestBody MerchantLoginRequest request) {
	    Merchant merchant = merchantAuthService.loginMerchant(request.getEmail(), request.getPassword());
	    if (merchant != null) {
	        MerchantDetailsResponse response = new MerchantDetailsResponse(merchant.getId(),merchant.getEmail(), merchant.getRole());
	        return ResponseEntity.ok(response);
	    }
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	            .body(new ErrorResponse("Invalid merchant credentials", HttpStatus.UNAUTHORIZED.value()));
	}
	
}

