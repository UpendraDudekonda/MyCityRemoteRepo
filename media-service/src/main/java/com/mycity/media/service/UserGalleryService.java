package com.mycity.media.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UserGalleryService 
{
   String uploadImages(MultipartFile file,Long userId,
		               Long placeId,
		               String placeName,String city,
		               String district,String state);
   List<String> getImages(String district);
   
   String deleteImage(Long imageId);
}
