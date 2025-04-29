package com.mycity.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> 
{
	List<Review> findByPlaceId(Long placeId);
}
