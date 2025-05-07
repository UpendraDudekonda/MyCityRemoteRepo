package com.mycity.client.email;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/email")
public class ClientEmailService {

	private final WebClient.Builder webClientBuilder;

	private static final String API_GATEWAY_SERVICE_NAME = "API-GATEWAY";; // Update with the actual localhost URL
																			// and port
	private static final String OTP_REQUEST_PATH = "/auth/email/send";

	private static final String OTP_VERIFY_PATH = "/auth/otp/verifyotp";

	// Constructor injection to initialize the WebClient.Builder
	public ClientEmailService(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@PostMapping("/request-otp")
	public Mono<String> requestOTP(@RequestBody RequestOtpDTO request) {
	    System.out.println("=== [requestOTP] ===");
	    System.out.println("[STEP 1] Received OTP request with body: " + request);

	    String fullUri = "lb://" + API_GATEWAY_SERVICE_NAME + OTP_REQUEST_PATH;
	    System.out.println("[STEP 2] Target URI: " + fullUri);

	    return createWebClient()
	            .post()
	            .uri(fullUri)
	            .body(Mono.just(request), RequestOtpDTO.class)
	            .retrieve()
	            .onStatus(HttpStatusCode::isError, clientResponse -> {
	                System.out.println("[STEP 3] Error Status Detected: " + clientResponse.statusCode());
	                return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
	                    System.out.println("[STEP 4] Error Response Body: " + errorBody);
	                    return Mono.error(new RuntimeException(
	                            "OTP request failed: " + clientResponse.statusCode() + " - " + errorBody));
	                });
	            })
	            .bodyToMono(String.class)
	            .doOnNext(responseBody -> {
	                System.out.println("[STEP 5] Successful Response Body: " + responseBody);
	            })
	            .doOnError(error -> {
	                System.out.println("[STEP 6] Exception during WebClient call: " + error.getMessage());
	            })
	            .doOnTerminate(() -> {
	                System.out.println("[STEP 7] WebClient request completed (success or fail)");
	            })
	            .onErrorResume(e -> {
	                System.out.println("[STEP 8] Returning fallback error: " + e.getMessage());
	                return Mono.just("OTP request failed: " + e.getMessage());
	            });
	}


	@PostMapping("/verify-otp")
	public Mono<String> verifyOTP(@RequestBody VerifyOtpDTO request) {
		return createWebClient().post()
				.uri("lb://" +API_GATEWAY_SERVICE_NAME + OTP_VERIFY_PATH) // Using the localhost URL
				.body(Mono.just(request), VerifyOtpDTO.class).retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
						.flatMap(errorBody -> Mono.error(new RuntimeException(
								"OTP verification failed: " + clientResponse.statusCode() + " - " + errorBody))))
				.bodyToMono(String.class).onErrorResume(e -> Mono.just("OTP verification failed: " + e.getMessage()));
	}

	// Method to create a WebClient instance
	private WebClient createWebClient() {
		return webClientBuilder.baseUrl(API_GATEWAY_SERVICE_NAME).build();
	}
}
