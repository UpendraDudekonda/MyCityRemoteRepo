package com.mycity.media.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.media.entity.Images;

public interface ImageServiceRepository extends JpaRepository<Images, Long>{
	


	Optional<Images> findFirstByCategoryIgnoreCaseOrderByImageIdAsc(String category);

	
	List<Images> findImagesByPlaceId(Long placeId);
	
    @Query("SELECT i.imageId FROM Images i WHERE i.placeId = :placeId")
    List<Long> findImageIdsByPlaceId(Long placeId);

	List<Images> findImagesByPlaceName(String placeName);


	List<Images> findByPlaceId(Long placeId);


}
