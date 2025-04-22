package com.mycity.media.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.Images;

public interface ImageServiceRepository extends JpaRepository<Images, Long>{
	

	List<String> findImageUrlsByPlaceId(Long placeId);

	Optional<Images> findFirstByCategoryIgnoreCaseOrderByImageIdAsc(String category);

}
