package com.mycity.shared.reviewdto;

import java.time.LocalDate;

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
//	   private Long reviewId;
//	   private Long userId;  // Automatically resolved using the userName; no need to include this in the request payload.
//	   private Long placeId; // Automatically resolved using the palceName; no need to include this in the request payload.
	   private String placeName;
	   private String userName;
	   private String reviewDescription;
	   private String imageUrl;
	   private LocalDate postedOn;
	       
}

