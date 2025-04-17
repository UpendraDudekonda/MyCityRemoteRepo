package com.mycity.shared.tripplannerdto;

import java.util.List;
import java.util.Map;

import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.weatherdto.WeatherDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanResponseDTO {
    private RouteDTO route;
    private List<PlaceDTO> suggestedPlaces;
    private WeatherDTO sourceWeather;
    private WeatherDTO destinationWeather;
    private Map<String, WeatherDTO> placesWeather;
    
}