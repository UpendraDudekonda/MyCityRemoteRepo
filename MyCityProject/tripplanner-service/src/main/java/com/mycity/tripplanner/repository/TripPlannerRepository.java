package com.mycity.tripplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.tripplanner.entity.TripPlanner;

public interface TripPlannerRepository extends JpaRepository<TripPlanner, Long>{
	
	

}
