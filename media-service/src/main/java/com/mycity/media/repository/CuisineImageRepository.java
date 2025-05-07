package com.mycity.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.media.entity.CuisineImages;

@Repository
public interface CuisineImageRepository extends JpaRepository<CuisineImages, Long>{

}
