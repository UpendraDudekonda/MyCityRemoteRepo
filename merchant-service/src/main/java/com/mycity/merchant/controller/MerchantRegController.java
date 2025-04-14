package com.mycity.merchant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.merchant.service.MerchantAuthInterface;
import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.merchantdto.MerchantRegRequest;

@RestController
@RequestMapping("/merchant")
public class MerchantRegController {
	
	@Autowired
    private MerchantAuthInterface merchantAuth;

    @PostMapping("/auth/internal/register")
    public ResponseEntity<?> registerUser(@RequestBody MerchantRegRequest merchantRequest) {
        try {
            String message = merchantAuth.registerMerchant(merchantRequest);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage(), 500));
        }
    }
}
