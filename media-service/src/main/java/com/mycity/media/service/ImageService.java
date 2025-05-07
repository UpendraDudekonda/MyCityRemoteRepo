package com.mycity.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.mediadto.ImageDTO;

public interface ImageService {

	void uploadImageForPlaces(MultipartFile file, Long placeId, String placeName, String category, String imageName);

	ImageDTO fetchImage(Long imageId);

	String getFirstImageUrlByCategory(String category);

	List<AboutPlaceImageDTO> getAboutPlaceImages(Long placeId);

	List<String> getAllImageUrlsByCategory(String category);

	void uploadImageForCuisines(MultipartFile file, Long placeId, Long cuisineId, String placeName, String category,
			String cuisineName);

}
