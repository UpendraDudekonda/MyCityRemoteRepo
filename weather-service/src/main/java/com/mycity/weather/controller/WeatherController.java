package com.mycity.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.tripplannerdto.CoordinateDto;
import com.mycity.shared.weatherdto.WeatherDTO;
import com.mycity.weather.service.WeatherServiceInterface;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherServiceInterface weatherService;

    @PostMapping("/by-coordinates")
    public ResponseEntity<WeatherDTO> getWeather(@RequestBody CoordinateDto coordinate) {
        WeatherDTO weather = weatherService.getWeatherByCoordinates(coordinate);
        return ResponseEntity.ok(weather);
    }
}
