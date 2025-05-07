package com.mycity.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.trip.entity.TripPlan;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {

}
