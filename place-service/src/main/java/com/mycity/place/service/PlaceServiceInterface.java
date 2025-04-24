package com.mycity.place.service;

import java.util.List;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceDTO;

public interface PlaceServiceInterface 
{
    String addPlace(PlaceDTO dto);
    Place getPlace(Long placeId);
    String updatePlace(Long placeId,PlaceDTO dto);
    String deletePlace(Long placeId);
	Place savePlace(Place place);
	Place getPlaceById(Long id);
	Object getAllPlaces();
	List<String> getAllDistinctCategories();
	
}
