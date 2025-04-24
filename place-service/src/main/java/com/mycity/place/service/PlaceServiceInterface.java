package com.mycity.place.service;

import java.util.List;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;

public interface PlaceServiceInterface 
{
    String addPlace(PlaceDTO dto);
    PlaceResponseDTO getPlace(Long placeId);
    String updatePlace(Long placeId,PlaceDTO dto);
    String deletePlace(Long placeId);
	Place savePlace(Place place);
	Place getPlaceById(Long id);
	
	List<PlaceResponseDTO> getAllPlaces();
	List<PlaceCategoryDTO> getAllDistinctCategories();

}