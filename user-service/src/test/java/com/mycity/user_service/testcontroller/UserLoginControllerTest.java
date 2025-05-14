package com.mycity.user_service.testcontroller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.shared.responsedto.LoginResponse;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.user.controller.UserLoginController;
import com.mycity.user.entity.User;
import com.mycity.user.exception.UserNotFoundException;
import com.mycity.user.service.UserAuthenticationInterface;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthenticationInterface userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginUser_Success() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole("USER");

        Mockito.when(userService.loginUser("john.doe@example.com", "Password123"))
                .thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/user/auth/internal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login Successful")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("WrongPassword");

        Mockito.when(userService.loginUser("john.doe@example.com", "WrongPassword"))
                .thenThrow(new IllegalArgumentException("Incorrect password"));

        // Act & Assert
        mockMvc.perform(post("/user/auth/internal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    public void testLoginUser_UserNotFound() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("Password123");

        Mockito.when(userService.loginUser("nonexistent@example.com", "Password123"))
                .thenThrow(new UserNotFoundException("User not found with email: nonexistent@example.com"));

        // Act & Assert
        mockMvc.perform(post("/user/auth/internal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid credentials")))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    public void testLoginUser_MissingEmail() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("");
        request.setPassword("Password123");

        mockMvc.perform(post("/user/auth/internal/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].defaultMessage")
                        .value(hasItem("Email is required")));
    }


    @Test
    public void testLoginUser_MissingPassword() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/user/auth/internal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password is required"));
    }
}


