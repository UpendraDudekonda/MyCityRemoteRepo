package com.mycity.media.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.Images;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.ImageServiceRepository;
import com.mycity.media.service.ImageService;
import com.mycity.shared.mediadto.ImageDTO;


@Service
public class ImageServiceImpl implements ImageService {
	
	@Autowired
    private CloudinaryHelper  cloudinaryHelper;
	
	@Autowired
	private ImageServiceRepository imageServiceRepository;

	@Override
	public void uploadImage(MultipartFile file, Long placeId, String placeName, String category,String imageName) {
		 // Upload to Cloudinary + save info in DB
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
	public ImageDTO fetchImage(Long imageId) {
	    Images image = imageServiceRepository.findById(imageId)
	        .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

	    return new ImageDTO(
	        image.getImageId(),
	        image.getImageUrl(),
	        image.getCategory(),
	        image.getPlaceName(),
	        image.getPlaceId()
	    );
	}

	@Override
	public List<String> getImagesByPlaceId(Long placeId) {
        List<Images> mediaList = imageServiceRepository.findByPlaceId(placeId);
        List<String> imageUrls = new ArrayList<>();
        for (Images media : mediaList) {
            imageUrls.add(media.getImageUrl());
        }
        return imageUrls;
    }



}
