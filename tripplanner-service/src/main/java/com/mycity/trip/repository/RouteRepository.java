package com.mycity.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.trip.entity.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long>{

}
