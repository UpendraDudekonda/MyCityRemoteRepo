package com.mycity.media.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycity.media.entity.ReviewImages;

import feign.Param;

public interface ReviewImageRepository extends JpaRepository<ReviewImages, Long> 
{
	List<ReviewImages> findByReviewId(Long reviewId);

	@Query("SELECT r.imageUrl FROM ReviewImages r WHERE r.placeId = :placeId")
	String findImageUrlByPlaceId(@Param("placeId") Long placeId);

}
