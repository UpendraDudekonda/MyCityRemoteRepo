package com.mycity.media.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.entity.Images;
import com.mycity.media.entity.ReviewImages;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.ReviewImageRepository;
import com.mycity.media.service.ReviewImageService;

@Service
public class ReviewImageServiceimpl implements ReviewImageService 
{
	
     @Autowired
     private CloudinaryHelper cloudinaryHelper;
    
	 @Autowired
     private ReviewImageRepository reviewRepo;
	   
	@Override
	public void uploadReviewImage(MultipartFile file, Long reviewId, Long placeId,
			                      String placeName, Long userId,String userName) 
	{
		if (file == null || file.isEmpty()) 
		{
		    throw new IllegalArgumentException("Uploaded file is empty or null.");
		}
		if (reviewId == null || placeId == null || userId == null) 
		{
		    throw new IllegalArgumentException("Review ID, Place ID, and User ID must not be null.");
		}
		
		String imageUrl = cloudinaryHelper.saveImage(file);
		
		ReviewImages image=new ReviewImages();
		image.setPlaceId(placeId);
		image.setPlaceName(placeName);
		image.setUserId(userId);
		image.setUserName(userName);
		image.setReviewId(reviewId);
		image.setImageUrl(imageUrl);
		
		//using reviewRepo to save
		reviewRepo.save(image);
	}

	@Override
	public String deleteReviewImage(Long reviewId) {
	    // Fetch all images associated with the given reviewId
	    List<ReviewImages> images = reviewRepo.findByReviewId(reviewId);

	    // Return message if no images are found
	    if (images == null || images.isEmpty()) {
	        return "No images found for reviewId: " + reviewId;
	    }

	    // Attempt to delete each image
	    int deletedCount = 0;
	    for (ReviewImages image : images) {
	        try {
	            reviewRepo.deleteById(image.getReviewImageId()); // Use the correct primary key
	            deletedCount++;
	        } catch (Exception e) {
	            // Log the error or handle it appropriately
	            System.err.println("Error deleting image with ID: " + image.getReviewImageId() + " - " + e.getMessage());
	        }
	    }

	    return deletedCount + " image(s) deleted for reviewId: " + reviewId;
	}



}
