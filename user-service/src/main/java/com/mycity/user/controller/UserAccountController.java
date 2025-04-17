package com.mycity.user.controller;

import com.mycity.shared.updatedto.UpdatePhoneRequest;
import com.mycity.user.service.UserAccountInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserAccountController {

    private final UserAccountInterface userAccountService;

    public UserAccountController(UserAccountInterface userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PatchMapping("/account/updatephone")
    public ResponseEntity<String> updatePhoneNumber(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UpdatePhoneRequest request) {

        System.out.println("📲 Updating phone number for userId: " + userId);

        boolean isUpdated = userAccountService.updatePhoneNumber(userId, request);
        if (!isUpdated) {
            System.out.println("❌ User not found for ID: " + userId);
            return ResponseEntity.badRequest().body("User not found");
        }

        System.out.println("✅ Phone number updated successfully for userId: " + userId);
        return ResponseEntity.ok("Phone number updated successfully");
    }
}
