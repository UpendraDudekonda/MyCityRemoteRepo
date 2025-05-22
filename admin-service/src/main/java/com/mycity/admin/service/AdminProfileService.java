package com.mycity.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.admindto.AdminProfileResponse;
import com.mycity.shared.admindto.AdminProfileUpdateDTO;

public interface AdminProfileService {

	AdminProfileResponse getAdminById(String adminId);

	void uploadUserImage(MultipartFile file, String userId);

	String getImageUrlByUserId(String userId);

	//void updateAdminFields(Long adminId, AdminProfileUpdateDTO request);

	void updateAdminFields(String adminId, AdminProfileUpdateDTO request);

}
