package com.mycity.review.service;

import java.util.List;

import com.mycity.shared.reviewdto.ReviewDTO;
import com.mycity.shared.reviewdto.ReviewSummaryDTO;

public interface ReviewServiceInterface 
{
   String addPlaceReview(ReviewDTO dto);
   String updateReview(Long reviewId,ReviewDTO dto);
   List<ReviewSummaryDTO> getUserReview(Long placeId);
   String deleteReview(Long reviewId);
}
