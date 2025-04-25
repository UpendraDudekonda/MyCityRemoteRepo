package com.mycity.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.shared.userdto.UserDTO;
import com.mycity.shared.userdto.UserLoginRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientUserProfileController
{
    private final WebClient.Builder webClientBuilder;
    private final CookieTokenExtractor cookieTokenExtractor;

    private static final Logger logger = LoggerFactory.getLogger(ClientUserProfileController.class);

    private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String USER_PROFILE_PATH_ON_GATEWAY = "/user/profile";
    private static final String USER_ID_FINDING_PATH="/user/getuserId/{userName}";
    private static final String USER_UPDATING_PATH="/user/updateuser/{userId}";
    private static final String USER_DELETING_PATH="/user/deleteuser/{userId}";
    private static final String USER_PASSWORD_CHANGING_PATH="/user/updatepassword";

    
    @GetMapping("/profile/user")
    public Mono<ResponseEntity<String>> getUserProfile(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) {

        // Extract token using the injected CookieTokenExtractor
        String token = cookieTokenExtractor.extractTokenFromCookie(cookie);

        if (token == null || token.isEmpty()) {
            logger.error("❌ Missing Authorization token from cookie");
            return Mono.just(ResponseEntity.status(400).body("Authorization token is missing"));
        }

        logger.info("📩 Forwarding Authorization token to API Gateway: {}", token);
        System.out.println(token);

        return webClientBuilder.build()
                .get()
                .uri("lb://" + API_GATEWAY_SERVICE_NAME + USER_PROFILE_PATH_ON_GATEWAY)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // Forward token to API Gateway
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    logger.info("✅ Received response from API Gateway with status: {}", response.getStatusCode());
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                })
                .onErrorResume(ex -> {
                    logger.error("❌ Error forwarding to API Gateway: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Something went wrong."));
                });
    }
    
    @GetMapping("/getid/{userName}")
	public Mono<String> getUserId(@PathVariable String userName,@RequestHeader(value = HttpHeaders.COOKIE) String cookie) 
    {
		System.out.println("ClientUserProfileController.getUserId()");
		//Using CookieTokenExtractor to get Token From the cookie
		String token=cookieTokenExtractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .get()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_ID_FINDING_PATH,userName)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Add Place: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Fetch User Id: " + e.getMessage()));
	}
    
	@PutMapping("/updateuser/{userId}")
	public Mono<String> updateUser(@PathVariable  String userId,@RequestBody UserDTO dto,
			@RequestHeader (value=HttpHeaders.COOKIE,required=false) String cookie) 
	{
		System.out.println("ClientUserProfileController.updateUser()");
		//using CookieTokenExtractor to Extract Token from Cookie
		String token=cookieTokenExtractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .put()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_UPDATING_PATH,userId)
	            .bodyValue(dto)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ token)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Update User: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to UPDATE User: " + e.getMessage()));
	}
	
    @DeleteMapping("/deleteUser/{userId}")
	public Mono<String> deleteUserById(@PathVariable String userId,@RequestHeader(value=HttpHeaders.COOKIE,required = false) String Cookie) 
    {
	    System.out.println("ClientUserProfileController.deleteUserById()");
	    //using cookieTokenExtractor to Extract Token from Cookie
	    String token=cookieTokenExtractor.extractTokenFromCookie(Cookie);
	    return webClientBuilder.build()
	            .delete()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_DELETING_PATH,userId)
	            .header(HttpHeaders.AUTHORIZATION,"Bearer "+ token)
	            .retrieve()                                                 
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Delete User: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to Delete User: " + e.getMessage()));
	}
    
	@PatchMapping("/updatepassword")
	public Mono<String> updateUserPassword(@RequestBody UserLoginRequest request
			,@RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie) 
	{
		System.out.println("ClientUserProfileController.updateUserPassword()");
		String token = cookieTokenExtractor.extractTokenFromCookie(cookie);
	    return webClientBuilder.build()
	            .patch()
	            .uri("lb://" +API_GATEWAY_SERVICE_NAME + USER_PASSWORD_CHANGING_PATH)
	            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
	            .bodyValue(request)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse ->
	                    clientResponse.bodyToMono(String.class)
	                            .flatMap(errorBody -> Mono.error(new RuntimeException("Failed to Update User Password: " + clientResponse.statusCode() + " - " + errorBody))))
	            .bodyToMono(String.class)
	            .onErrorResume(e -> Mono.just("Failed to UPDATE User Password: " + e.getMessage()));
	}
    
}
