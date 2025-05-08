package com.mycity.media.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserProfileImgService {

	void uploadUserImage(MultipartFile file, String userId);

	String getImageUrlByUserId(String userId);

}
