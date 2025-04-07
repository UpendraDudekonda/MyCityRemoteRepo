package com.mycity.shared.placedto;

import com.mycity.shared.timezonedto.TimezoneDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private Long placeId;
    private String name;
    private String about;
    private String history;
    private TimezoneDTO timezone;
    private String category;
    private Double latitude;
    private Double longitude;
}

