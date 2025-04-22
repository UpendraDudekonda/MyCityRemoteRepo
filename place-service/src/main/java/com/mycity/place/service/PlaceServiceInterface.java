package com.mycity.place.service;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceDTO;

public interface PlaceServiceInterface 
{
    String addPlace(PlaceDTO dto);
    Place getPlace(Long placeId);
    String updatePlace(Long placeId,PlaceDTO dto);
    String deletePlace(Long placeId);
}
