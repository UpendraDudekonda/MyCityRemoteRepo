package com.mycity.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface CuisineImageService 
{
  String uploadCuisineImages(MultipartFile file,Long placeId,String placeName,String category,String cuisineName);
}
