package com.mycity.user.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.entity.User;
import com.mycity.user.exception.UserNotFoundException;
import com.mycity.user.repository.UserAuthRepository;
import com.mycity.user.service.UserAuthenticationInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserAuthenticationInterface {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient.Builder webClientBuilder;

    private static final String OTP_SERVICE_BASE = "lb://OTP-SERVICE";
    private static final String EMAIL_SERVICE_BASE = "lb://EMAIL-SERVICE";

    private static final String REQUEST_OTP_PATH = "/auth/generateotp";
    private static final String VERIFY_OTP_PATH = "/auth/verifyotp";

    @Override
    public String registerUser(UserRegRequest user) {
        validateUserInput(user);

        if (userAuthRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User newUser = new User();
        newUser.setUsername(user.getFirstname() + user.getLastname());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setMobilenumber(user.getMobilenumber());
        newUser.setCreatedDate(LocalDateTime.now());
        newUser.setUpdatedDate(LocalDateTime.now());
        newUser.setRole("USER");

        userAuthRepository.save(newUser);
        return "User registered successfully";
    }

    @Override
    public User loginUser(String email, String password) {
        User user = userAuthRepository.findByEmail(email);
        
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return user;
    }

    // Validation logic for user input
    private void validateUserInput(UserRegRequest user) {
        if (user.getFirstname() == null || user.getFirstname().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getMobilenumber() == null || !user.getMobilenumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits");
        }
    }
    //Not Using Code
    @Override
    public void startRegistration(RequestOtpDTO request) {
        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        try {
            webClientBuilder.build()
                .post()
                .uri(EMAIL_SERVICE_BASE + REQUEST_OTP_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Wait for the response from Email service
        } catch (WebClientResponseException ex) {
            String errorMsg = "Error occurred while requesting OTP: " + ex.getMessage();
            if (ex.getStatusCode().is5xxServerError()) {
                throw new IllegalStateException("Email service is temporarily unavailable. Please try again later.", ex);
            } else if (ex.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Invalid request to the Email service: " + ex.getMessage(), ex);
            } else {
                throw new IllegalArgumentException(errorMsg, ex);
            }
        }
    }

    @Override
    public void completeRegistration(UserRegRequest request, String otp) {
        validateUserInput(request);

        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Boolean isValid = null;

        try {
            isValid = webClientBuilder.build()
                .post()
                .uri(OTP_SERVICE_BASE + VERIFY_OTP_PATH)
                .bodyValue(new VerifyOtpDTO(request.getEmail(), otp))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false) // fallback in case of exception
                .block();
        } catch (WebClientResponseException ex) {
            String errorMsg = "Error occurred while verifying OTP: " + ex.getMessage();
            if (ex.getStatusCode().is5xxServerError()) {
                throw new IllegalStateException("OTP service is temporarily unavailable. Please try again later.", ex);
            } else if (ex.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Invalid OTP verification request: " + ex.getMessage(), ex);
            } else {
                throw new IllegalArgumentException(errorMsg, ex);
            }
        }

        if (Boolean.FALSE.equals(isValid)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // OTP is valid → register the user
        registerUser(request);
    }
}
