package com.mycity.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.admin.service.AdminProfileService;
import com.mycity.shared.admindto.AdminProfileResponse;


@RestController
@RequestMapping("/admin")
public class AdminProfileController {

    private final AdminProfileService adminProfile;

    public AdminProfileController(AdminProfileService adminProfile) {
        this.adminProfile = adminProfile;
    }

    @GetMapping("/profile")
    public ResponseEntity<AdminProfileResponse> getAdminProfile(@RequestHeader("X-User-Id") String adminId) {

        System.out.println("admin profile request got into methods for retrieving");

        AdminProfileResponse admin = adminProfile.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }

}