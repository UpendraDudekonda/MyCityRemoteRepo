package com.mycity.shared.rating.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    private Long ratingId;
    private Long userId;
    private Long placeId;
    private Integer ratingValue;
    private String comment;
    private LocalDateTime postedDateTime;
}

