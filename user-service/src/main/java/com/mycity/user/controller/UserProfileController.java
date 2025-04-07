package com.mycity.user.controller;

import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.service.UserProfileInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserProfileInterface userProfile;

    public UserProfileController(UserProfileInterface userProfile) {
        this.userProfile = userProfile;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("X-User-Id") String userId) {
        UserResponseDTO user = userProfile.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
