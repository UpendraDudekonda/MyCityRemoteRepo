package com.mycity.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.auth.config.JwtService;
import com.mycity.shared.admindto.AdminDetailsResponse;
import com.mycity.shared.admindto.AdminLoginRequest;
import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.responsedto.LoginResponse;
import com.mycity.shared.merchantdto.MerchantDetailsResponse;
import com.mycity.shared.merchantdto.MerchantLoginRequest;
import com.mycity.shared.userdto.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthLoginController {

    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    private static final String USER_SERVICE = "lb://USER-SERVICE";
    private static final String ADMIN_SERVICE = "lb://ADMIN-SERVICE";
    private static final String MERCHANT_SERVICE = "lb://MERCHANT-SERVICE";

    @PostMapping("/login/user")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        try {
            
            LoginResponse response = webClientBuilder.build()
                .post()
                .uri(USER_SERVICE + "/user/auth/internal/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    res -> res.bodyToMono(ErrorResponse.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(LoginResponse.class)
                .block();

            // Check if the response indicates successful login
            if (response != null && Boolean.TRUE.equals(response.getStatus())) {
               
                String token = jwtService.generateToken(response.getId(), request.getEmail(), response.getRole());
                return ResponseEntity.ok(token);
            }

            throw new RuntimeException("Invalid user credentials");
        } catch (RuntimeException e) {
           
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
    }

    @PostMapping("/login/merchant")
    public ResponseEntity<?> loginMerchant(@RequestBody MerchantLoginRequest request) {
        try {
            MerchantDetailsResponse response = webClientBuilder.build()
                .post()
                .uri(MERCHANT_SERVICE + "/merchant/auth/internal/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    res -> res.bodyToMono(ErrorResponse.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(MerchantDetailsResponse.class)
                .block();

            if (response != null) {
                String token = jwtService.generateToken(response.getId(),request.getEmail(), response.getRole());
                return ResponseEntity.ok(token);
            }

            throw new RuntimeException("Invalid merchant credentials");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
    }
    
    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
        try {
            AdminDetailsResponse response = webClientBuilder.build()
                .post()
                .uri(ADMIN_SERVICE + "/admin/auth/internal/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    res -> res.bodyToMono(ErrorResponse.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(AdminDetailsResponse.class)
                .block();

            if (response != null) {
                String token = jwtService.generateToken(response.getId(),request.getEmail(), response.getRole());
                return ResponseEntity.ok(token);
            }

            throw new RuntimeException("Invalid admin credentials");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
        }
    }

}
