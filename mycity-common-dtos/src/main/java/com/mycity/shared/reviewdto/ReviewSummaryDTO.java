package com.mycity.shared.reviewdto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReviewSummaryDTO 
{
	   private String placeName;
	   private String userName;
	   private String reviewDescription;
	   private String imageUrl;
	   private Double rating;
	   private String userImageUrl;
	   private LocalDate postedOn;
}
