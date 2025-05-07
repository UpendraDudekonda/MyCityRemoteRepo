package com.mycity.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceCategoryDTO;

import feign.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	@Query("SELECT NEW com.mycity.shared.placedto.PlaceCategoryDTO(p.placeId, p.placeName, p.placeHistory, p.categoryId, p.categoryName) FROM Place p")
	List<PlaceCategoryDTO> findPlaceIdsAndCategories();

	@Query("SELECT p.placeId FROM Place p WHERE p.placeName = :placeName")
	Optional<Long> findPlaceIdByPlaceName(@Param("placeName") String placeName);

	Optional<Long> findPlaceIdByPlaceNameIgnoreCase(String placeName);

}
