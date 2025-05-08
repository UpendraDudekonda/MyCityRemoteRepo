package com.mycity.shared.placedto;

import java.util.List;

import com.mycity.shared.mediadto.AboutPlaceImageDTO;

import lombok.Data;

@Data
public class PlaceDiscoveriesResponeDTO 
{
  private String placeName;
  private String placeCategory;
  private List<AboutPlaceImageDTO> placeRelatedImages;
  private long placeId;
}
