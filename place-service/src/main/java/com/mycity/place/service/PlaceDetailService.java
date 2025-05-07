package com.mycity.place.service;

import java.util.List;
import java.util.Map;

import com.mycity.place.entity.Place;

public interface PlaceDetailService {

	Map<String, Object> getPlaceDetails(Long placeId);

	List<Place> getNearbyPlaces(String placeName, double radiusKm);

}
