package com.mycity.weather.service;


import com.mycity.shared.tripplannerdto.CoordinateDto;
import com.mycity.shared.weatherdto.WeatherDTO;

public interface WeatherServiceInterface {
    WeatherDTO getWeatherByCoordinates(CoordinateDto coordinate);
}
