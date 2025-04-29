package com.mycity.client.auth;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.shared.merchantdto.MerchantRegRequest;
import com.mycity.client.exception.CustomRegistrationException;
import com.mycity.shared.errordto.ErrorResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientRegistrationService {

    private final WebClient.Builder webClientBuilder;

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON

    @PostMapping("/register/user")
    public Mono<?> registerUser(@RequestBody UserRegRequest request) {
        return forwardRegister(request,"/auth/register/user", UserRegRequest.class);
    }

    @PostMapping("/register/merchant")
    public Mono<?> registerMerchant(@RequestBody MerchantRegRequest request) {
        return forwardRegister(request, "/auth/register/merchant", MerchantRegRequest.class);
    }



    private <T> Mono<String> forwardRegister(T request, String path, Class<T> typeclass) {
        return webClientBuilder.build()
            .post()
            .uri("lb://" + API_GATEWAY_SERVICE_NAME + path)
            .body(Mono.just(request), typeclass)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res -> 
                res.bodyToMono(String.class)
                   .flatMap(body -> {
                       try {
                           // Parse the JSON string to get just the "message"
                           Map<String, Object> map = objectMapper.readValue(body, Map.class);
                           String message = (String) map.getOrDefault("message", "Unknown error");
                           ErrorResponse error = new ErrorResponse(message, 400);
                           return Mono.error(new CustomRegistrationException(error));
                       } catch (Exception e) {
                           return Mono.error(new CustomRegistrationException(
                               new ErrorResponse("Invalid error response format", 400)));
                       }
                   })
            )
            .bodyToMono(String.class);
    }

  
}
