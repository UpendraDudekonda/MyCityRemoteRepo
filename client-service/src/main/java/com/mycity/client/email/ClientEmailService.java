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
	private static final String OTP_REQUEST_PATH = "/auth/request-otp/user";

	private static final String OTP_VERIFY_PATH = "/auth/verify-otp/user";

	// Constructor injection to initialize the WebClient.Builder
	public ClientEmailService(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@PostMapping("/request-otp")
	public Mono<String> requestOTP(@RequestBody RequestOtpDTO request) {
		System.out.println("Request to API Gateway with body: " + request);

		// Directly use localhost URL to avoid load balancer resolving
		return createWebClient().post().uri("lb://" + API_GATEWAY_SERVICE_NAME + OTP_REQUEST_PATH) // Directly specify
																									// localhost
				.body(Mono.just(request), RequestOtpDTO.class).retrieve().onStatus(HttpStatusCode::isError,
						clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
							System.out.println("Error occurred: " + clientResponse.statusCode() + " - " + errorBody);
							return Mono.error(new RuntimeException(
									"OTP request failed: " + clientResponse.statusCode() + " - " + errorBody));
						}))
				.bodyToMono(String.class).doOnTerminate(() -> System.out.println("WebClient request completed"))
				.onErrorResume(e -> {
					System.out.println("Error during WebClient call: " + e.getMessage());
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
