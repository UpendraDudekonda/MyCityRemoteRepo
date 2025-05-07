//package com.mycity.user_service.testcontroller;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mycity.shared.userdto.UserRegRequest;
//import com.mycity.user.controller.UserRegistrationController;
//import com.mycity.user.service.UserAuthenticationInterface;
//// Assuming this is the correct path
//
//@WebMvcTest(UserRegistrationController.class) // Target the actual controller
//class UserRegistrationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserAuthenticationInterface userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser
//    void registerUser_Success() throws Exception {
//        UserRegRequest request = new UserRegRequest();
//        request.setFirstname("John");
//        request.setLastname("Doe");
//        request.setEmail("uppi@gmail.com");
//        request.setPassword("password");
//        request.setMobilenumber("1234567890");
//        request.setOtpVerified(true);
//
//        when(userService.registerUser(any(UserRegRequest.class)))
//               .thenReturn("User registered successfully");
//
//        mockMvc.perform(post("/user/auth/internal/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User registered successfully"));
//    }
//
//    @Test
//    @WithMockUser
//    void registerUser_BadRequest() throws Exception {
//        UserRegRequest request = new UserRegRequest();
//        request.setFirstname("John");
//        request.setLastname("Doe");
//        request.setPassword("password");
//        request.setMobilenumber("1234567890");
//        request.setOtpVerified(true);
//
//        when(userService.registerUser(any(UserRegRequest.class)))
//               .thenThrow(new IllegalArgumentException("Invalid user data"));
//
//        mockMvc.perform(post("/user/auth/internal/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message", is("Invalid user data")))
//                .andExpect(jsonPath("$.status", is(400)));
//    }
//}