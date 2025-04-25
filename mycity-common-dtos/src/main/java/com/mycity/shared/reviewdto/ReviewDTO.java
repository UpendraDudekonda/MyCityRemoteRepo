package com.mycity.shared.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO 
{
	   private Long reviewId;
	   private Long userId;
	   private Long placeId;
	   private String placeName;
	   private String userName;
	   private String reviewDescription;
	   private String imageUrl;
	       
}

