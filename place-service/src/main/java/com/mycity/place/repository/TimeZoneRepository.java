package com.mycity.place.repository;
 
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;
 
 import com.mycity.place.entity.TimeZone;
 
 @Repository
 public interface TimeZoneRepository extends JpaRepository<TimeZone, Long>{
 
 }