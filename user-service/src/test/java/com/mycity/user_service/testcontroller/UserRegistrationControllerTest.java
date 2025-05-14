package com.mycity.user_service.testcontroller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.controller.UserRegistrationController;
import com.mycity.user.service.UserAuthenticationInterface;

public class UserRegistrationControllerTest {

    private UserRegistrationController controller;
    private UserAuthenticationInterface userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserAuthenticationInterface.class);
        controller = new UserRegistrationController();
        controller.getClass().getDeclaredFields()[0].setAccessible(true);
        // Injecting the mock manually as there's no Spring context
        try {
            controller.getClass().getDeclaredField("userService").set(controller, userService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock", e);
        }
    }

    private UserRegRequest buildValidUserRequest() {
        UserRegRequest user = new UserRegRequest();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setMobilenumber("9876543210");
        return user;
    }

    @Test
    void testRegisterUser_Success() {
        UserRegRequest user = buildValidUserRequest();
        when(userService.registerUser(user)).thenReturn("User registered successfully");

        ResponseEntity<?> response = controller.registerUser(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("User registered successfully");
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        UserRegRequest user = buildValidUserRequest();
        when(userService.registerUser(user)).thenThrow(new IllegalArgumentException("Email already registered"));

        ResponseEntity<?> response = controller.registerUser(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getMessage()).isEqualTo("Email already registered");
        assertThat(error.getStatus()).isEqualTo(400);
    }

    @Test
    void testRegisterUser_InvalidInput() {
        UserRegRequest user = buildValidUserRequest();
        user.setFirstname(""); // Invalid input
        when(userService.registerUser(user)).thenThrow(new IllegalArgumentException("First name is required"));

        ResponseEntity<?> response = controller.registerUser(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getMessage()).isEqualTo("First name is required");
        assertThat(error.getStatus()).isEqualTo(400);
    }

    @Test
    void testRegisterUser_InternalError() {
        UserRegRequest user = buildValidUserRequest();
        when(userService.registerUser(user)).thenThrow(new RuntimeException("Database failure"));

        ResponseEntity<?> response = controller.registerUser(user);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getMessage()).isEqualTo("Database failure");
        assertThat(error.getStatus()).isEqualTo(500);
    }
}



