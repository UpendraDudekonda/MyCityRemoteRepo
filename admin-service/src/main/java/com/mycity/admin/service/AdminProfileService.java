package com.mycity.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.admindto.AdminProfileResponse;

public interface AdminProfileService {

	AdminProfileResponse getAdminById(String adminId);

	void uploadUserImage(MultipartFile file, String userId);

	String getImageUrlByUserId(String userId);

}
