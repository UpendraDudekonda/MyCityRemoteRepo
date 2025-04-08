package com.mycity.shared.weatherdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {

    private Long weatherId;
    private Long placeId;
    private String bestTimeToVisit;
    private String bestSeason;
}

