package com.mycity.media.service;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.mediadto.ImageDTO;

public interface ImageService {

	void uploadImage(MultipartFile file, Long placeId, String placeName, String category);

	ImageDTO fetchImage(Long imageId);

	String getFirstImageUrlByCategory(String category);

}
