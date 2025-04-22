package com.mycity.media.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.Images;


public interface ImageServiceRepository extends JpaRepository<Images, Long>{

	List<Images> findByPlaceId(Long placeId);
	

}
