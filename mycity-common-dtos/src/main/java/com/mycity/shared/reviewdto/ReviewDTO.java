package com.mycity.shared.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO 
{
	   private String placeName;
	   private String userName;
	   private String reviewDescription;
	   private String imageUrl;
	       
}

