 package com.mycity.client.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode; // Import HttpStatusCode

import com.mycity.shared.emaildto.ForgotPasswordDTO;
import com.mycity.shared.emaildto.ResetPasswordRequest;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/client/forgotpassword")
@AllArgsConstructor
public class ClientForgotPasswordController {

	private static final Logger log = LoggerFactory.getLogger(ClientForgotPasswordController.class);

	private final WebClient.Builder webClientBuilder;

    // Use static final fields for service name and paths
    private static final String APIGATEWAY_SERVICE_NAME = "API-GATEWAY"; // Use the actual service ID registered in discovery
    private static final String INITIATE_PATH = "/auth/forgot-password/initiate";
    private static final String RESET_PATH = "/auth/forgot-password/reset";


    @PostMapping("/initiate")
    public Mono<ResponseEntity<String>> initiateForgotPassword(@RequestBody ForgotPasswordDTO request) {
        // Build the URI using lb:// prefix and the service name and path
        String uri = "lb://" + APIGATEWAY_SERVICE_NAME + INITIATE_PATH;

        return webClientBuilder.build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                // Corrected: Use HttpStatusCode::is4xxClientError or response.statusCode()::is4xxClientError
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Error response from authentication service (4xx) for {}: {}", uri, errorBody);
                                    return Mono.error(new RuntimeException("Error from authentication service: " + errorBody));
                                }))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("An error occurred while initiating forgot password for {}: {}", uri, e.getMessage());
                    String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson));
                });
    }

    @PostMapping("/reset")
    public Mono<ResponseEntity<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
         // Build the URI using lb:// prefix and the service name and path
        String uri = "lb://" + APIGATEWAY_SERVICE_NAME + RESET_PATH;

        return webClientBuilder.build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                 // Corrected: Use HttpStatusCode::is4xxClientError or response.statusCode()::is4xxClientError
                 .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Error response from authentication service (4xx) for {}: {}", uri, errorBody);
                                    return Mono.error(new RuntimeException("Error from authentication service: " + errorBody));
                                }))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                 .onErrorResume(e -> {
                     log.error("An error occurred while resetting password for {}: {}", uri, e.getMessage());
                     String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
                     return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson));
                 });
    }


}
