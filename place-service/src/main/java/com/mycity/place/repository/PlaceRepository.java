package com.mycity.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place,Long> 
{

	@Query("SELECT DISTINCT p.placeCategory FROM Place p")
	List<String> findDistinctPlaceCategories();


}
