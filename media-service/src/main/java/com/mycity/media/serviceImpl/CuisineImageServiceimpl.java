package com.mycity.media.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.CuisineImages;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.CuisineImageRepository;
import com.mycity.media.service.CuisineImageService;

@Service
public class CuisineImageServiceimpl implements CuisineImageService 
{
	@Autowired
	private CuisineImageRepository cuisineRepo;
	
	@Autowired
	private CloudinaryHelper cloudianryHelper;

	@Override
	public String uploadCuisineImages(MultipartFile file, Long placeId, String placeName,
	                                  String category, String cuisineName) {
	    
	    if (file == null || file.isEmpty()) {
	        throw new IllegalArgumentException("Uploaded file is empty or null");
	    }
	    if (placeId == null || placeName == null || category == null || cuisineName == null) {
	        throw new IllegalArgumentException("Required metadata(placeId (or) placeName (or) category (or) cuisineName) is missing");
	    }

	    try {
	        // Upload the image to Cloudinary
	        String imageUrl = cloudianryHelper.saveImage(file);

	        // Create CuisineImages entity
	        CuisineImages cuisine = new CuisineImages();
	        cuisine.setCuisineId(0);  // Hardcoded
	        cuisine.setCuisineName(cuisineName);
	        cuisine.setPlaceId(placeId);
	        cuisine.setPlaceName(placeName);
	        cuisine.setCategory(category);
	        cuisine.setImageUrl(imageUrl);

	        // Save to repository
	        CuisineImages savedCuisine = cuisineRepo.save(cuisine);
	        return "Cuisine with name '" + savedCuisine.getCuisineName() + "' saved successfully.";

	    } 
	    catch (Exception e) 
	    {
	        
	        throw new RuntimeException("Failed to upload and save cuisine image", e);
	    }
	}


}
