package com.mycity.trip.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration; // Correct Java 8+ Duration import
import java.util.stream.IntStream; // Import IntStream

import org.springframework.beans.factory.annotation.Autowired; // Import for @Autowired
import org.springframework.beans.factory.annotation.Value; // Import for @Value
import org.springframework.http.HttpHeaders; // Correct Spring HttpHeaders import

import org.springframework.http.MediaType; // Correct Spring MediaType import
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Assuming ALL shared DTOs are in this SINGLE package
import com.mycity.shared.tripplannerdto.ActivityDto;
import com.mycity.shared.tripplannerdto.DayItineraryDto;
import com.mycity.shared.tripplannerdto.DayPlanDto;
import com.mycity.shared.tripplannerdto.ExternalApiRequestDto;
import com.mycity.shared.tripplannerdto.ExternalApiResponseDto;
import com.mycity.shared.tripplannerdto.PlannedActivityDto;
import com.mycity.shared.tripplannerdto.TripRequestDto;
import com.mycity.shared.tripplannerdto.TripResponseDto;


import com.mycity.trip.entity.TripPlan; // Assuming entity is here
import com.mycity.trip.repository.TripPlanRepository; // Assuming repository is here
import com.mycity.trip.service.TripPlannerService; // Assuming service interface is here

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry; // Correct import for Reactor Retry strategy

import lombok.extern.slf4j.Slf4j; // Assuming Lombok is correctly set up

@Service
@Slf4j // For logging
// No explicit constructor here. Java provides a default no-argument constructor
// because there are no uninitialized 'final' fields.
// Spring uses this no-argument constructor and then injects dependencies via fields.
public class TripPlannerServiceImpl implements TripPlannerService {

    // --- Field Injection ---
    // 'final' keyword is REMOVED because fields are not initialized in a constructor

    // @Autowired is technically optional for field injection from Spring 4.3+
    // if there's only one suitable bean, but it's good practice to be explicit.
	
    @Autowired
    private WebClient webClient; // Spring will inject the WebClient bean

    @Value("${external.trip-planner.api-key}") // Spring will inject value from config
    private String externalApiKey;

    @Autowired
    private TripPlanRepository tripPlanRepository; // Spring will inject the Repository bean

    @Autowired
    private ObjectMapper objectMapper; // Spring will inject the ObjectMapper bean

    // No explicit constructor is defined.

    // --- Service Method Implementation ---
    @Override // Implement the interface method
    public Mono<TripResponseDto> planTrip(TripRequestDto requestDto) {
         log.info("Received request to plan trip from {} to {} on {}-{} (using Field Injection)",
                 requestDto.getSource(), requestDto.getDestination(), requestDto.getStartDate(), requestDto.getEndDate());

        // 1. Map internal DTO to External API's request DTO
        ExternalApiRequestDto externalRequest = mapToExternalApiRequest(requestDto);
        log.debug("Mapped internal request to external API format: {}", externalRequest);

        // 2. Call the External API
        // The webClient is injected directly as a field now
        return webClient.post()
                .uri("/plan") // Specific endpoint path for planning on the external API
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + externalApiKey) // Example auth header
                .contentType(MediaType.APPLICATION_JSON) // Use Spring's MediaType
                .bodyValue(externalRequest) // Send the request body
                .retrieve() // Execute the request and get the response
                // --- Error Handling using onStatus ---
                // These methods handle HTTP status codes returned by the external API
                .onStatus(status -> status.is4xxClientError(), response ->
                    // Handle client errors (4xx) from the external API (e.g., Bad Request, Unauthorized)
                    response.bodyToMono(String.class) // Read the error response body
                            .flatMap(body -> {
                                log.error("External API returned client error {}: {}", response.statusCode(), body);
                                // Return a Mono error to propagate the exception
                                return Mono.error(new RuntimeException("External API Client Error: " + response.statusCode() + " - " + body));
                            })
                )
                 .onStatus(status -> status.is5xxServerError(), response ->
                    // Handle server errors (5xx) from the external API (e.g., Internal Server Error)
                    response.bodyToMono(String.class) // Read the error response body (optional, but good for debugging)
                            .flatMap(body -> {
                                log.error("External API returned server error {}: {}", response.statusCode(), body);
                                // Return a Mono error to propagate the exception
                                return Mono.error(new RuntimeException("External API Server Error: " + response.statusCode() + " - " + body));
                            })
                 )
                // If status is not handled by onStatus, proceed to bodyToMono
                .bodyToMono(ExternalApiResponseDto.class) // Deserialize the response body into our DTO
                // --- Retry Logic ---
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)) // Retry up to 3 times with 2s initial backoff
                           .filter(throwable -> {
                               // Filter specifies which exceptions should trigger a retry
                               // Retry only on WebClientResponseException (HTTP errors) and specifically 5xx server errors
                               boolean shouldRetry = throwable instanceof WebClientResponseException &&
                                   ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
                               if (shouldRetry) {
                                   log.warn("Retrying external API call due to 5xx error: {}", throwable.getMessage());
                               }
                               return shouldRetry;
                           })
                           // Define what happens if retries are exhausted
                           .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                log.error("Max retries exhausted for external API call after {} attempts", retrySignal.totalRetries(), retrySignal.failure());
                                return new RuntimeException("External API call failed after multiple retries", retrySignal.failure());
                           })
                )
                // --- General Error Mapping ---
                .onErrorMap(throwable -> {
                    // Catch any other errors that occurred in the chain (e.g., network issues before status,
                    // JSON deserialization errors, errors from the retry exhausted handler)
                    log.error("Unexpected error during external API call or processing", throwable);
                    // Map the original exception to a custom runtime exception
                    return new RuntimeException("Error calling external trip planner API", throwable);
                })
                // 3. Process the External API's response upon successful completion
                .flatMap(externalResponse -> {
                    log.info("Successfully received response from external API for tripId: {}", externalResponse.getTripId());
                    // Map the external response DTO to your internal response DTO
                    TripResponseDto processedResponse = mapToTripResponseDto(externalResponse);
                    log.debug("Mapped external response to internal format");

                    // 4. Optionally save the processed plan
                    // NOTE: tripPlanRepository.save() is typically a BLOCKING operation in Spring Data JPA.
                    // Calling a blocking operation inside a reactive chain (flatMap) can block the event loop
                    // and degrade performance. For a fully reactive application, you would need:
                    // a) A reactive database driver and repository (e.g., R2DBC with Spring Data R2DBC).
                    // b) To offload the blocking save operation to a dedicated scheduler/thread pool
                    //    using subscribeOn(Schedulers.boundedElastic()).
                    // For this example, we keep the blocking save for simplicity, but be aware of this.
                    saveTripPlan(requestDto, processedResponse, externalResponse.getTripId());
                    log.info("Trip plan saved locally with external ID: {}", externalResponse.getTripId());

                    // Return the processed response wrapped in a Mono
                    return Mono.just(processedResponse);
                });
    }

    // --- Helper method to map your internal request DTO to the external API's request DTO ---
    private ExternalApiRequestDto mapToExternalApiRequest(TripRequestDto requestDto) {
       // This is where you translate fields from your DTO to the external API's DTO.
       // Field names, date/time formats, enum values might differ.
       // Use Lombok's Builder to create the external request DTO.
        return ExternalApiRequestDto.builder()
                 .departureLocation(requestDto.getSource()) // Map 'source' to 'departureLocation'
                 .arrivalLocation(requestDto.getDestination()) // Map 'destination' to 'arrivalLocation'
                 .tripStart(requestDto.getStartDate()) // Map startDate to tripStart
                 .tripEnd(requestDto.getEndDate())     // Map endDate to tripEnd
                 .mode(requestDto.getTravelMode())   // Map travelMode to mode (ensure value compatibility)
                 .preferences(requestDto.getInterests()) // Map interests to preferences (ensure format compatibility)
                 // ... map other fields required by the external API
                 .build(); // Build the DTO instance
    }

    // --- Helper method to map the External API's response DTO back to your internal response DTO ---
    private TripResponseDto mapToTripResponseDto(ExternalApiResponseDto externalResponse) {
       // Translate fields from the external API's response DTO to your internal DTO.
       // Also, transform the structure if needed (e.g., flatten data, enrich with your own info)
       // Use Lombok's Builder to create your internal response DTO.

        // Use IntStream to iterate with index for the day summary
         List<DayPlanDto> dailyPlans = IntStream.range(0, externalResponse.getItinerary().size())
                 // Explicitly define the return type of the mapToObj lambda and cast the result
                 .mapToObj(index -> {
                     // Get the DayItineraryDto for the current index - Use the DTO from the same package
                     DayItineraryDto day = externalResponse.getItinerary().get(index);
                     List<PlannedActivityDto> activities = mapActivities(day.getActivities()); // Map activities for this day

                     // Build the DayPlanDto step-by-step
                     DayPlanDto.DayPlanDtoBuilder builder = DayPlanDto.builder();
                     builder.date(day.getDate());
                     builder.summary("Day " + (index + 1)); // Use the index for the day summary (1-based)
                     builder.activities(activities); // Set the mapped activities

                     return builder.build(); // Build and return the DayPlanDto
                 })
                 .collect(Collectors.toList()); // Collect daily plans into a list

         return TripResponseDto.builder()
                 .planId(externalResponse.getTripId()) // Use external ID for now, or generate internal
                 .tripSummary(externalResponse.getSummary()) // Map external summary
                 .dailyPlans(dailyPlans) // Include the list of daily plans
                 .build(); // Build the TripResponseDto
    }

    // --- Helper method to map a list of external ActivityDto to internal PlannedActivityDto ---
    // Assuming external API ActivityDto is in com.mycity.shared.tripplannerdto
    private List<PlannedActivityDto> mapActivities(List<ActivityDto> externalActivities) {
        return externalActivities.stream()
                .map(activity -> PlannedActivityDto.builder()
                        .name(activity.getName())
                        .location(activity.getLocation())
                        .startTime(activity.getStartTime())
                        .endTime(activity.getEndTime()) // Map endTime
                       // .duration(activity.getDuration()) // Map duration if available
                        .description(activity.getDescription())
                        .category(activity.getType()) // Map external type to your internal category
                        // If external API provides lat/lon in ActivityDto, map them here:
                        // Ensure your external ActivityDto has getLatitude() and getLongitude()
                        // and your internal PlannedActivityDto builder has latitude() and longitude()
                        // .latitude(activity.getLatitude()) // Example
                        // .longitude(activity.getLongitude()) // Example
                        .build())
                .collect(Collectors.toList()); // Collect mapped activities into a list
    }


    // --- Helper method to save the trip plan ---
    // NOTE: This method contains a BLOCKING database save operation.
    // In a fully reactive application, consider using R2DBC or offloading.
    private void saveTripPlan(TripRequestDto requestDto, TripResponseDto responseDto, String externalTripId) {
        log.debug("Preparing to save trip plan with external ID: {}", externalTripId);
        TripPlan tripPlan = new TripPlan();
        // You would typically link this to a user, e.g.:
        // tripPlan.setUserId(getCurrentUserId());

        tripPlan.setExternalApiId(externalTripId);
        tripPlan.setSource(requestDto.getSource()); // Use getSource()
        tripPlan.setDestination(requestDto.getDestination());
        tripPlan.setStartDate(requestDto.getStartDate());
        tripPlan.setEndDate(requestDto.getEndDate());
        tripPlan.setCreatedDate(LocalDate.now());

        try {
            // Serialize the processed itinerary DTO to a JSON string for storage
            tripPlan.setItineraryJson(objectMapper.writeValueAsString(responseDto));
            log.debug("Serialized itinerary JSON successfully");
        } catch (JsonProcessingException e) {
            // Log error if serialization fails
            log.error("Error serializing itinerary for saving", e);
            // Depending on requirements, you might re-throw or handle differently.
            // For this example, we log and continue, but the itinerary JSON will be null.
        }

        try {
            // Save the entity to the database using the JPA repository (BLOCKING call)
            tripPlanRepository.save(tripPlan);
            log.debug("Successfully saved trip plan to database");
        } catch (Exception e) {
             // Log any errors during the database save operation
             log.error("Error saving trip plan to database", e);
             // Handle database save errors (e.g., transaction rollback)
        }
    }
}
