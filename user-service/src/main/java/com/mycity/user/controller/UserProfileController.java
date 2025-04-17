package com.mycity.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.service.UserProfileInterface;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileInterface userProfile;

    public UserProfileController(UserProfileInterface userProfile) {
        this.userProfile = userProfile;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("X-User-Id") String userId) {
        UserResponseDTO user = userProfile.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
  
    
}
