package com.mycity.shared.placedto;

import java.util.List;

import com.mycity.shared.timezonedto.TimezoneDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {

    private long placeId;

   
    private String placeName;

  
    private String aboutPlace;

    private String placeHistory;

    private TimezoneDTO timeZone;

    private Double rating;

   
    private String placeCategory;

    private String placeDistrict;

    private CoordinateDTO coordinate;

    private List<String> photoUrls;
}
