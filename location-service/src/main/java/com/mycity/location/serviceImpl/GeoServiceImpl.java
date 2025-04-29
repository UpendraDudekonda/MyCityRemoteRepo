package com.mycity.location.serviceImpl;

import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.mycity.location.entity.Location;
import com.mycity.location.repository.GeoRepositroy;
import com.mycity.location.service.GeoServiceInterface;
import com.mycity.shared.locationdto.GeoRequestDTO;
import com.mycity.shared.tripplannerdto.CoordinateDTO;

import reactor.core.publisher.Mono;

@Service
public class GeoServiceImpl implements GeoServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(GeoServiceImpl.class);

    @Value("${geo.api.base-url}")
    private String nominatimApiUrl;

    private final WebClient webClient;
    private final GeoRepositroy geoRepository;

    // Constructor injection for WebClient and GeoRepository
    public GeoServiceImpl(WebClient.Builder webClientBuilder, GeoRepositroy geoRepository) {
        this.webClient = webClientBuilder.baseUrl(nominatimApiUrl).build(); // Set base URL for WebClient
        this.geoRepository = geoRepository;
    }

    @Override
    @Cacheable(value = "locationCache", key = "#city.toLowerCase()") // Cache based on city
    public Mono<CoordinateDTO> getCoordinatesByCity(String city) { // Renamed method to use city
        Optional<Location> cached = geoRepository.findByCityIgnoreCase(city); // Query based on city
        if (cached.isPresent()) {
            Location loc = cached.get();
            return Mono.just(new CoordinateDTO(loc.getLatitude(), loc.getLongitude()));
        }

        return fetchFromApiAndStore(city);
    }

    private Mono<CoordinateDTO> fetchFromApiAndStore(String city) {
        // Build the full URI as a String (safer for deprecated avoidance)
        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("nominatim.openstreetmap.org")
                .path("/search")
                .queryParam("q", city)
                .queryParam("format", "json")
                .queryParam("addressdetails", "1")
                .queryParam("limit", "1")
                .build()
                .toUriString();

        logger.debug("Nominatim API URI: {}", uri);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        if (!array.isEmpty()) {
                            JSONObject firstResult = array.getJSONObject(0);
                            double lat = Double.parseDouble(firstResult.getString("lat"));
                            double lon = Double.parseDouble(firstResult.getString("lon"));

                            JSONObject addressObj = firstResult.getJSONObject("address");

                            // Extracting the most specific city/town/village name
                            String resolvedCity = Optional.ofNullable(addressObj.optString("city", null))
                                    .orElse(Optional.ofNullable(addressObj.optString("town", null))
                                    .orElse(Optional.ofNullable(addressObj.optString("village", null))
                                    .orElse(city))); // fallback to input
                            
//                            String district = Optional.ofNullable(addressObj.optString("district", null))
//                                    .orElse(Optional.ofNullable(addressObj.optString("county", null))
//                                    .orElse(Optional.ofNullable(addressObj.optString("state_district", null))
//                                    .orElse(null)));

                            String state = addressObj.optString("state", null);
                            String country = addressObj.optString("country", null);
                            
                            

                            Location location = new Location();
                            location.setLatitude(lat);
                            location.setLongitude(lon);
//                            location.setDistrictName(district);
                            location.setCity(resolvedCity);
                            location.setState(state);
                            location.setCountry(country);
                            

                            geoRepository.save(location);
                            return Mono.just(new CoordinateDTO(lat, lon));
                        }
                        logger.warn("Nominatim returned empty result for city: {}", city);
                        return Mono.empty();
                    } catch (Exception e) {
                        logger.error("Error parsing Nominatim response for city: {}", city, e);
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Error calling Nominatim API for city: {}", city, e);
                    return Mono.empty();
                });
    }


    @Override
    public Mono<Map<String, CoordinateDTO>> getCoordinatesForSourceAndDestination(GeoRequestDTO requestDTO) {
        Mono<CoordinateDTO> sourceMono = getCoordinatesByCity(requestDTO.getSourceplace()); // Use city-based method
        Mono<CoordinateDTO> destinationMono = getCoordinatesByCity(requestDTO.getDestinationplace()); // Use city-based method

        return Mono.zip(sourceMono, destinationMono)
                .map(tuple -> Map.of("source", tuple.getT1(), "destination", tuple.getT2()));
    }
}