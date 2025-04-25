package com.mycity.shared.ratingdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO 
{
  
    private String userName;
    private String placeName;
    private Double ratingValue;
    private String comment;
}

