package com.mycity.auth.controller;

import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.shared.merchantdto.MerchantRegRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.shared.errordto.ErrorResponse;

@RestController
@RequestMapping("/auth")
public class AuthRegisterController {

	@Autowired
	private WebClient webClient;  // Inject WebClient instance directly.


    private static final String USER_SERVICE = "lb://USER-SERVICE";
    private static final String MERCHANT_SERVICE = "lb://MERCHANT-SERVICE";

    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody UserRegRequest user) {
        try {
            String response = webClient
                .post()
                .uri(USER_SERVICE + "/user/auth/internal/register")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            try {
                // Parse the error body returned by internal user service
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse error = mapper.readValue(ex.getResponseBodyAsString(), ErrorResponse.class);
                return ResponseEntity.status(ex.getStatusCode()).body(error);

            } catch (Exception parseException) {
                // If error body couldn't be parsed
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("User registration failed: " + ex.getMessage(), 500));
            }
        } catch (Exception ex) {
            // Handle other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("User registration failed: " + ex.getMessage(), 500));
        }
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<?> registerMerchant(@RequestBody MerchantRegRequest merchant) {
        try {
            String response = webClient
                .post()
                .uri(MERCHANT_SERVICE + "/merchant/auth/internal/register")
                .bodyValue(merchant)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            try {
                // Try parsing the error body from the internal merchant service
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse error = mapper.readValue(ex.getResponseBodyAsString(), ErrorResponse.class);
                return ResponseEntity.status(ex.getStatusCode()).body(error);

            } catch (Exception parseException) {
                // If parsing fails, return generic error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Merchant registration failed: " + ex.getMessage(), 500));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Merchant registration failed: " + ex.getMessage(), 500));
        }
    }
}
