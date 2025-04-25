package com.mycity.trip.serviceImpl;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.locationdto.GeoRequestDTO;
import com.mycity.shared.placedto.PlaceDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;
import com.mycity.shared.tripplannerdto.RouteDTO;
import com.mycity.shared.tripplannerdto.TripPlanResponseDTO;
import com.mycity.shared.tripplannerdto.TripRequestDTO;
import com.mycity.shared.weatherdto.WeatherDTO;
import com.mycity.trip.service.TripPlannerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripPlannerServiceImpl implements TripPlannerService {

    private final WebClient.Builder webClientBuilder;

    private static final String PLACE_SERVICE_URL = "http://PLACE-SERVICE/places/by-route";
    private static final String WEATHER_SERVICE_URL = "http://WEATHER-SERVICE/weather/by-coordinates";
    private static final String LOCATION_SERVICE_URL = "http://LOCATION-SERVICE/location/geocode";

    @Override
    public Mono<ResponseEntity<TripPlanResponseDTO>> generateTripPlan(TripRequestDTO request) {
        int numberOfDays = request.getNumberOfDays();
        if (numberOfDays <= 0 && request.getStartDate() != null && request.getEndDate() != null) {
            numberOfDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        }

        log.info("📆 Planning trip from '{}' to '{}' for {} day(s) from {} to {}",
                request.getSource(),
                request.getDestination(),
                numberOfDays,
                request.getStartDate(),
                request.getEndDate());

        GeoRequestDTO geoRequestDTO = new GeoRequestDTO();
        geoRequestDTO.setSourceplace(request.getSource());
        geoRequestDTO.setDestinationplace(request.getDestination());

        return getCoordinatesFromLocationService(geoRequestDTO)
                .flatMap(coords -> {
                    CoordinateDTO sourceCoord = coords.get("source");
                    CoordinateDTO destCoord = coords.get("destination");

                    RouteDTO route = new RouteDTO(
                            List.of(sourceCoord, destCoord),
                            "mocked_polyline", // Placeholder, to be replaced by map integration
                            100.0,
                            120.0,
                            List.of(), // Add waypoints if needed
                            request.getSource(),
                            request.getDestination()
                    );

                    Mono<List<PlaceDTO>> placesMono = webClientBuilder.build()
                            .post()
                            .uri(PLACE_SERVICE_URL)
                            .bodyValue(route)
                            .retrieve()
                            .bodyToFlux(PlaceDTO.class)
                            .collectList()
                            .onErrorReturn(List.of());

                    Mono<WeatherDTO> sourceWeatherMono = getWeather(sourceCoord);
                    Mono<WeatherDTO> destinationWeatherMono = getWeather(destCoord);

                    return Mono.zip(placesMono, sourceWeatherMono, destinationWeatherMono)
                            .flatMap(tuple -> {
                                List<PlaceDTO> places = tuple.getT1();
                                WeatherDTO sourceWeather = tuple.getT2();
                                WeatherDTO destinationWeather = tuple.getT3();

                                return Flux.fromIterable(places)
                                        .flatMap(place ->
                                                getWeather(place.getCoordinate())
                                                    .map(weather -> Map.entry(place.getPlaceName(), weather)))
                                        .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                                        .map(weatherMap -> ResponseEntity.ok(
                                                new TripPlanResponseDTO(route, places, sourceWeather, destinationWeather, weatherMap)
                                        ));
                            });
                });
    }

    private Mono<Map<String, CoordinateDTO>> getCoordinatesFromLocationService(GeoRequestDTO geoRequestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(LOCATION_SERVICE_URL)
                .bodyValue(geoRequestDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, CoordinateDTO>>() {})
                .onErrorResume(e -> {
                    log.error("Failed to fetch coordinates for source/destination: {}", e.getMessage());
                    return Mono.just(Map.of(
                            "source", new CoordinateDTO(0.0, 0.0),
                            "destination", new CoordinateDTO(0.0, 0.0)
                    ));
                });
    }

    private Mono<WeatherDTO> getWeather(CoordinateDTO coord) {
        if (coord == null) return Mono.just(new WeatherDTO("Unknown", "Unknown", "No data", 0.0));

        return webClientBuilder.build()
                .post()
                .uri(WEATHER_SERVICE_URL)
                .bodyValue(coord)
                .retrieve()
                .bodyToMono(WeatherDTO.class)
                .onErrorReturn(new WeatherDTO("No Location", "Unavailable", "Error fetching weather", 0.0));
    }
}

