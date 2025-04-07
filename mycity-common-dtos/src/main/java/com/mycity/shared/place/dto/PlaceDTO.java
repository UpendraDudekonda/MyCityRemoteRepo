package com.mycity.shared.place.dto;

import com.mycity.shared.timezone.dto.TimezoneDTO;

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

