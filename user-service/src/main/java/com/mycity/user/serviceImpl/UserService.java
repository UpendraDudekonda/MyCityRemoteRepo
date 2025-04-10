package com.mycity.user.serviceImpl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.config.JwtService;
import com.mycity.user.entity.User;
import com.mycity.user.repository.UserAuthRepository;
import com.mycity.user.service.UserServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

	private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient.Builder webClientBuilder;

    // Define constants for API Gateway URL and paths
   // Replace with the actual service name if required
    private static final String EMAIL_SERVICE_NAME = "EMAIL-SERVICE"; 
    private static final String OTP_SERVICE_NAME = "OTP-SERVICE"; // URL to your email service

    private static final String OTP_REQUEST_PATH = "/auth/request-otp/user"; // Path for sending OTP
    private static final String OTP_VERIFY_PATH = "/auth/verify-otp/user";   // Path for verifying OTP
;

	private final JwtService jwtservice;


//	private boolean isValidEmail(String email) {
//		// A more robust email validation using a regular expression is recommended
//		return email.contains("@") && email.contains(".");
//	}

	@Override
	public String LoginUser(UserLoginRequest request) {
		String email = request.getEmail();
		String password = request.getPassword();

		// Find the user by email
		User user = userAuthRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		// Validate the password
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		System.out.println("Login Successfull");
		// Authentication successful, generate JWT token
		return jwtservice.generateToken(user.getId(), user.getEmail(), user.getRole());
	}
    // Step 1: Start Registration (Send OTP)
    @Override
    public void startRegistration(UserRegRequest request) {
        // Check if email already exists
        if (userAuthRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email.");
        }

        // Call email-service to send OTP
        webClientBuilder.build()
            .post()
            .uri("lb://" +EMAIL_SERVICE_NAME + OTP_REQUEST_PATH)  // Construct the URL for OTP request
            .bodyValue(Map.of("email", request.getEmail()))  // Send the email in the request body
            .retrieve()
            .bodyToMono(String.class)  // Response body type
            .block();  // Blocking until the response is received

        System.out.println("OTP sent to email. Awaiting verification.");
    }

    // Step 2: Complete Registration (Verify OTP and Register User)
    @Override
    public void completeRegistration(UserRegRequest request, String otp) {
        // Verify OTP via email-service
        Boolean isValid = webClientBuilder.build()
            .post()
            .uri("lb://" +OTP_SERVICE_NAME + OTP_VERIFY_PATH)  // Construct the URL for OTP verification
            .bodyValue(Map.of("email", request.getEmail(), "otp", otp))  // Send email and OTP in the body
            .retrieve()
            .bodyToMono(Boolean.class)  // Response body type (true or false)
            .block();

        if (Boolean.FALSE.equals(isValid)) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        // Now, register the user
        User user = new User();
        user.setUsername(request.getFirstname() + " " + request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setRole("USER");

        userAuthRepository.save(user);

        System.out.println("User registered after OTP verification.");
    }
}
