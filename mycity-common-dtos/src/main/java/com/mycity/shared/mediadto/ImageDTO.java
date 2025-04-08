package com.mycity.shared.mediadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private Long imageId;
    private String imageUrl;
    private String placeName;
    private String category;
    private Long placeId;
}

