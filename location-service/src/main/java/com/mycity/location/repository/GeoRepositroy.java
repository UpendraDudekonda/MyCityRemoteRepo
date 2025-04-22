package com.mycity.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.location.entity.Location;

@Repository
public interface GeoRepositroy extends JpaRepository<Location, Long>{

	

	Optional<Location> findByCityIgnoreCase(String city);

}

