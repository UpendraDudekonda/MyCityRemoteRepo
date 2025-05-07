package com.mycity.client.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.client.user.ClientUserProfileController;
import com.mycity.shared.updatedto.UpdatePhoneRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientAccountController {
	
	 	private final WebClient.Builder webClientBuilder;
	    private final CookieTokenExtractor cookieTokenExtractor;

	    private static final Logger logger = LoggerFactory.getLogger(ClientUserProfileController.class);

	    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
//	    private static final String USER_UPDATE_PATH = "/user/account/updatephone";


	
	@PatchMapping("/account/updatephone")
    public Mono<ResponseEntity<String>> updateUserPhone(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            @RequestBody UpdatePhoneRequest request) {

		
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);

        if (token == null || token.isEmpty()) {
            logger.error("❌ Missing Authorization token from cookie");
            return Mono.just(ResponseEntity.status(400).body("Authorization token is missing"));
        }

       System.out.println("📩 Forwarding phone update to API Gateway with token: {}"+token);
        System.out.println("📞 New Phone Number: {}"+request.getPhoneNumber());

        return webClientBuilder.build()
                .patch()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + "/user/account/updatephone")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("✅ Phone number update response: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                })
                .onErrorResume(ex -> {
                    logger.error("❌ Error updating phone number: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Something went wrong."));
                });
    }
}
