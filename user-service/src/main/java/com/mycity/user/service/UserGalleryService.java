package com.mycity.user.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.shared.placedto.UserGalleryDTO;

public interface UserGalleryService 
{
	String uploadImagesToGallery(List<MultipartFile> images,UserGalleryDTO dto);
	List<String> getImagesByDistrictName(String districtName);
	String deleteImage(Long imageId);
}
