package com.mycity.place.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceDTO;

public interface PlaceServiceInterface 
{
    String addPlace(PlaceDTO dto);
    Place getPlace(Long placeId);
    String updatePlace(Long placeId,PlaceDTO dto);
    String deletePlace(Long placeId);
	String addPlace(PlaceDTO placeDto, List<MultipartFile> images);
}
