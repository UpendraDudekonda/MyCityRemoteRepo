package com.mycity.media.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.mediadto.ImageDTO;

public interface ImageService {

	void uploadImage(MultipartFile file, Long placeId, String placeName, String category, String imageName);

	ImageDTO fetchImage(Long imageId);

	String getFirstImageUrlByCategory(String category);

	List<String> getAboutPlaceImages(Long placeId);

	String deleteImage(Long placeId);
}
