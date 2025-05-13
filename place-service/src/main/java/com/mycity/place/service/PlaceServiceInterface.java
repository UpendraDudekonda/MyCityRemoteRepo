package com.mycity.place.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceCategoryDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.placedto.PlaceRelatedImagesDTO;
import com.mycity.shared.placedto.PlaceResponseDTO;
import com.mycity.shared.placedto.PlaceWithImagesDTO;

import reactor.core.publisher.Flux;

public interface PlaceServiceInterface {
	String addPlace(PlaceDTO dto);

	PlaceResponseDTO getPlace(Long placeId);

	String updatePlace(Long placeId, PlaceDTO dto);

	String deletePlace(Long placeId);

	Place savePlace(Place place);

	Place getPlaceById(Long id);

	List<PlaceResponseDTO> getAllPlaces();

	List<PlaceCategoryDTO> getAllDistinctCategories();

	String addPlace(PlaceDTO placeDto, List<MultipartFile> images);


	Long getPlaceIdByName(String placeName);


	String addPlace(PlaceDTO placeDto, List<MultipartFile> placeImages, Map<String, MultipartFile> cuisineImages);

	String addPlace(PlaceDTO placeDto, Map<String, MultipartFile> placeImages, Map<String, MultipartFile> cuisineImages,
			Map<String, MultipartFile> hotelImages);

	List<PlaceWithImagesDTO> getPlacesByCategoryWithImages(String categoryName);

	Flux<PlaceResponseDTO> getPlacesByCategoryId(String categoryId);


}