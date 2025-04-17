package com.mycity.admin.service;

import com.mycity.shared.admindto.AdminProfileResponse;

public interface AdminProfileService {

	AdminProfileResponse getAdminById(String adminId);

}
