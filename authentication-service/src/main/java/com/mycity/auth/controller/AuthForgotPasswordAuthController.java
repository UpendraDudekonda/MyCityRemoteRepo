package com.mycity.auth.controller;




import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.emaildto.ForgotPasswordDTO;
import com.mycity.shared.emaildto.ResetPasswordRequest;
import com.mycity.shared.userdto.UserDetailsResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/forgot-password")
@RequiredArgsConstructor
public class AuthForgotPasswordAuthController {
	
	private final WebClient.Builder webClientBuilder;

    // Use static final field for user service name and path
    private static final String USER_SERVICE_NAME = "USER-SERVICE"; // Use the actual service ID
    private static final String GET_USER_BY_EMAIL_PATH = "/users/details/by-email/{email}";


    // This endpoint now directly checks if the user exists in the user-service
    @PostMapping("/initiate")
    public Mono<ResponseEntity<String>> initiateForgotPassword(@RequestBody ForgotPasswordDTO request) {
        WebClient userServiceClient = webClientBuilder.baseUrl("lb://" + USER_SERVICE_NAME).build();

        // Make a non-blocking call to check if the user exists
        return userServiceClient.get()
//                .uri(GET_USER_BY_EMAIL_PATH, request.getEmail()) // Pass email as a URI variable
        			.uri(uriBuilder -> uriBuilder
        			    .path("/users/details/by-email")
        			    .queryParam("email", request.getEmail())
        			    .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    // If user service returns 404, it means user not found.
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        // Return a Mono error with a specific message for user not found
                        return Mono.error(new RuntimeException("User with email " + request.getEmail() + " not found."));
                    } else {
                         // For other 4xx errors, propagate an exception with error body
                         return response.bodyToMono(String.class).flatMap(errorBody ->
                            Mono.error(new RuntimeException("Error from user service (" + response.statusCode() + "): " + errorBody)));
                    }
                })
                 .onStatus(HttpStatusCode::is5xxServerError, response ->
                     // Handle 5xx server errors from user service
                     response.bodyToMono(String.class).flatMap(errorBody ->
                            Mono.error(new RuntimeException("Server error from user service (" + response.statusCode() + "): " + errorBody))))
                .bodyToMono(UserDetailsResponse.class) // Expecting UserDetail if found
                .map(userDetail -> {
                    // If we reach here, the user was found (2xx response)
                    return ResponseEntity.ok("User found. Proceed with OTP.");
                })
                .onErrorResume(RuntimeException.class, e -> {
                    // Handle the exceptions thrown in onStatus or other errors
                    if (e.getMessage() != null && e.getMessage().contains("User with email")) {
                         // Specific handling for user not found error from onStatus
                        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                    } else {
                         // Handle other runtime exceptions during the WebClient call
                        return Mono.just(ResponseEntity.internalServerError().body("Error checking user existence: " + e.getMessage()));
                    }
                });
    }

    
    @PostMapping("/reset")
    public Mono<ResponseEntity<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        WebClient userServiceClient = webClientBuilder.baseUrl("lb://" + USER_SERVICE_NAME).build();

        return userServiceClient.post()
                .uri("/users/reset-password")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(errorBody ->
                            Mono.error(new RuntimeException("Error from user service (4xx): " + errorBody))))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class).flatMap(errorBody ->
                            Mono.error(new RuntimeException("Error from user service (5xx): " + errorBody))))
                .bodyToMono(String.class)  // <--- This line returns the actual body content
                .map(body -> ResponseEntity.ok(body))
                .onErrorResume(e ->
                    Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Password reset failed: " + e.getMessage())));
    }


	    

}
