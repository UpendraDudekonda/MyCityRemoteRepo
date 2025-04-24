package com.mycity.shared.placedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceCategoryDTO {
    private Long placeId;
    private String placeCategory;
}
