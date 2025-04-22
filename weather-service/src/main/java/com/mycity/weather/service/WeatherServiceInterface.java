package com.mycity.weather.service;

import com.mycity.shared.tripplannerdto.CoordinateDTO;
import com.mycity.shared.weatherdto.WeatherDTO;

public interface WeatherServiceInterface {
    WeatherDTO getWeatherByCoordinates(CoordinateDTO coordinate);
}
