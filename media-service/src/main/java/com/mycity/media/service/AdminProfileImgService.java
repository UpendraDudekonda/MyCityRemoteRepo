package com.mycity.media.service;




import org.springframework.web.multipart.MultipartFile;


public interface AdminProfileImgService {
	
	void uploadUserImage(MultipartFile file, String adminId);

	String getImageUrlByAdminId(String adminId);



}
