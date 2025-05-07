package com.mycity.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.emaildto.ResetPasswordRequest;
import com.mycity.shared.userdto.UserDetailsResponse;
import com.mycity.user.serviceImpl.UserForgotpasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserForgotPasswordController {
	
	private final UserForgotpasswordService userService;
//
//    @GetMapping("/details/by-email/{email}")
//    public ResponseEntity<UserDetailsResponse> getUserByEmail(@PathVariable String email) {
//    	UserDetailsResponse userDetail = userService.findUserByEmail(email);
//        if (userDetail != null) {
//            return ResponseEntity.ok(userDetail);
//        }
//        return ResponseEntity.notFound().build();
//    }
	
	@GetMapping("/details/by-email")
	public ResponseEntity<UserDetailsResponse> getUserByEmail(@RequestParam String email) {
	    UserDetailsResponse userDetail = userService.findUserByEmail(email);
	    if (userDetail != null) {
	        return ResponseEntity.ok(userDetail);
	    }
	    return ResponseEntity.notFound().build();
	}

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("password reset successfull...!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }
	

}
