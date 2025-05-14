package com.mycity.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceCategoryDTO;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	@Query("SELECT NEW com.mycity.shared.placedto.PlaceCategoryDTO(p.placeId, p.placeName, p.placeHistory, p.categoryId, p.categoryName) FROM Place p")
	List<PlaceCategoryDTO> findPlaceIdsAndCategories();

	@Query("SELECT p.placeId FROM Place p WHERE p.placeName = :placeName")
	Long findPlaceIdByPlaceName(@Param("placeName") String placeName);

	@Query("SELECT p.placeId FROM Place p WHERE LOWER(p.placeName) = LOWER(:placeName)")
	Long findPlaceIdByPlaceNameIgnoreCase(String placeName);


	Optional<Place> findByPlaceName(String placeName);

	@Query("SELECT p FROM Place p WHERE LOWER(p.categoryName) = LOWER(:categoryName)")
	List<Place> findByCategoryName(@Param("categoryName") String categoryName);

	List<Place> findAllByCategoryId(Long categoryId); // now it matches the entity field type





}
