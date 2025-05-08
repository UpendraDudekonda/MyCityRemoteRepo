package com.mycity.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.auth.config.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthLogoutController {
	
	@Autowired
    private JwtService jwtService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Create an empty JWT cookie to remove the token from the browser
        response.addCookie(new Cookie("token", ""));
        
        // Send the expired JWT cookie to clear it on the client side
        response.addHeader("Set-Cookie", jwtService.getCleanJwtCookie().toString());

        // Return success response
        return ResponseEntity.ok("Logged out successfully");
    }

}
