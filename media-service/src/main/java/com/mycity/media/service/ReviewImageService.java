package com.mycity.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface ReviewImageService 
{
	void uploadReviewImage(MultipartFile file,Long reviewId,Long placeId,String placeName,Long userId,String userName);
	String deleteReviewImage(Long reviewId);
	String getReviewForPlace(long placeId);
}
