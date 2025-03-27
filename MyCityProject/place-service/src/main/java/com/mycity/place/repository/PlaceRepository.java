package com.mycity.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
