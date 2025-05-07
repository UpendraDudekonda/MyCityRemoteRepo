package com.mycity.media.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.CuisineImages;
import com.mycity.media.entity.Images;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.CuisineImageRepository;
import com.mycity.media.repository.ImageServiceRepository;
import com.mycity.media.service.ImageService;
import com.mycity.shared.mediadto.AboutPlaceImageDTO;
import com.mycity.shared.mediadto.ImageDTO;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private CloudinaryHelper cloudinaryHelper;

	@Autowired
	private ImageServiceRepository imageServiceRepository;

	@Autowired
	private CuisineImageRepository cuisineImageRepository;

	@Override
	public void uploadImageForPlaces(MultipartFile file, Long placeId, String placeName, String category,
			String imageName) {

		String imageUrl = cloudinaryHelper.saveImage(file); // your Cloudinary logic

		Images img = new Images();
		img.setImageUrl(imageUrl);
		img.setPlaceId(placeId);
		img.setPlaceName(placeName);
		img.setCategory(category);
		img.setImageName(imageName);

		imageServiceRepository.save(img);
	}

	@Override
	public void uploadImageForCuisines(MultipartFile file, Long placeId, Long cuisineId, String placeName,
			String category, String cuisineName) {
		// Upload to Cloudinary (your existing logic)
		String imageUrl = cloudinaryHelper.saveImage(file);

		// Create and populate CuisineImages entity
		CuisineImages cuisineImage = new CuisineImages();
		cuisineImage.setCuisineId(cuisineId); // ✅ NEW: Set cuisine ID
		cuisineImage.setImageUrl(imageUrl);
		cuisineImage.setPlaceId(placeId);
		cuisineImage.setPlaceName(placeName);
		cuisineImage.setCategory(category);
		cuisineImage.setCuisineName(cuisineName);
		try {
			cuisineImageRepository.save(cuisineImage);
			System.out.println("✅ Uploaded and saved image for cuisineId: " + cuisineId);
		} catch (Exception e) {
			System.err.println("❌ Failed to save cuisine image: " + e.getMessage());
		}

	}

	@Override
	public ImageDTO fetchImage(Long imageId) {
		Images image = imageServiceRepository.findById(imageId)
				.orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

		return new ImageDTO(image.getImageId(), image.getImageUrl(), image.getCategory(), image.getPlaceName(),
				image.getPlaceId());
	}

	@Override
	public String getFirstImageUrlByCategory(String category) {
		return imageServiceRepository.findFirstByCategoryIgnoreCaseOrderByImageIdAsc(category).map(Images::getImageUrl)
				.orElse(null);
	}

	@Override
	public List<AboutPlaceImageDTO> getAboutPlaceImages(Long placeId) {
	    // Fetch all images associated with the placeId
	    List<Images> images = imageServiceRepository.findImagesByPlaceId(placeId);

	    // Map to DTOs
	    List<AboutPlaceImageDTO> imageDTOs = new ArrayList<>();
	    for (Images image : images) {
	        AboutPlaceImageDTO dto = new AboutPlaceImageDTO();
	        dto.setImageUrl(image.getImageUrl());   // Assuming Images has getImageUrl()
	        dto.setImageName(image.getImageName()); // Assuming Images has getImageName()
	        imageDTOs.add(dto);
	    }

	    return imageDTOs;
	}

	@Override
	public List<String> getAllImageUrlsByCategory(String category) {
		// TODO Auto-generated method stub
		return null;
	}

}
