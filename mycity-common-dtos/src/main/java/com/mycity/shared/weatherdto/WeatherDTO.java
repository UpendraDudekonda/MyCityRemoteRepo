package com.mycity.shared.weatherdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
	private String location;
    private String condition; // e.g., Sunny, Rainy
    private String description; // more detailed
    private double temperature; // in Celsius
}
