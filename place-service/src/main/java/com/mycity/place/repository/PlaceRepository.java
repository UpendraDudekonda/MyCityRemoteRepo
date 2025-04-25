package com.mycity.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place,Long> 
{   
    @Query("SELECT p.placeId FROM Place p WHERE LOWER(p.placeName) = LOWER(:placeName)")
    Long findPlaceIdByPlaceNameIgnoreCase(String placeName);
}
