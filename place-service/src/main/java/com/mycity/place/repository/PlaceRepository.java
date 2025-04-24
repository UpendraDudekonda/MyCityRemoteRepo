package com.mycity.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.place.entity.Place;
import com.mycity.shared.placedto.PlaceCategoryDTO;

public interface PlaceRepository extends JpaRepository<Place,Long> 
{

	

	@Query("SELECT NEW com.mycity.shared.placedto.PlaceCategoryDTO(p.placeId, p.placeCategory) FROM Place p")
	List<PlaceCategoryDTO> findPlaceIdsAndCategories();



}
