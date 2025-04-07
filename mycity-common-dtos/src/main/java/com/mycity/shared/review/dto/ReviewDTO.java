package com.mycity.shared.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long reviewId;
    private Long userId;
    private Long placeId;
    private String reviewDescription;
    private String imageUrl;
}

