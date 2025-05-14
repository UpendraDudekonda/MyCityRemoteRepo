package com.mycity.user_service.testcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.controller.UserRegistrationController;
import com.mycity.user.service.UserAuthenticationInterface;

@WebMvcTest(UserRegistrationController.class)
public class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthenticationInterface userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegRequest buildValidUser() {
        UserRegRequest user = new UserRegRequest();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("securePassword");
        user.setMobilenumber("1234567890");
        user.setOtpVerified(true);
        return user;
    }

    @Test
    @DisplayName("Test successful registration")
    public void testRegisterUserSuccess() throws Exception {
        UserRegRequest request = buildValidUser();

        Mockito.when(userService.registerUser(any(UserRegRequest.class)))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/user/auth/internal/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @DisplayName("Test registration with existing email - BadRequest")
    public void testRegisterUserEmailAlreadyExists() throws Exception {
        UserRegRequest request = buildValidUser();

        Mockito.when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already registered"));

        mockMvc.perform(post("/user/auth/internal/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Test registration throws internal error")
    public void testRegisterUserInternalError() throws Exception {
        UserRegRequest request = buildValidUser();

        Mockito.when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/user/auth/internal/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.status").value(500));
    }
}


