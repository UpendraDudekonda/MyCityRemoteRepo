package com.mycity.media.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mycity.media.entity.ReviewImages;

public interface ReviewImageRepository extends JpaRepository<ReviewImages, Long> 
{
	List<ReviewImages> findByReviewId(Long reviewId);
}
