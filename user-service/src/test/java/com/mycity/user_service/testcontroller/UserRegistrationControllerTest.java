package com.mycity.user_service.testcontroller;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import com.mycity.shared.errordto.ErrorResponse;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.controller.UserRegistrationController;
import com.mycity.user.service.UserAuthenticationInterface;
 
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserRegistrationControllerTest {
 
    @Mock
    private UserAuthenticationInterface userService;
 
    @InjectMocks
    private UserRegistrationController userRegistrationController;
 
    private UserRegRequest userRegRequest;
 
    @BeforeEach
    public void setUp() {
        userRegRequest = new UserRegRequest();
        userRegRequest.setFirstname("John");
        userRegRequest.setLastname("Doe");
        userRegRequest.setEmail("john.doe@example.com");
        userRegRequest.setPassword("Password123");
        userRegRequest.setMobilenumber("1234567890");
        userRegRequest.setOtpVerified(true);
    }
 
    @Test
    public void testRegisterUser_Success() {
 
        when(userService.registerUser(any(UserRegRequest.class))).thenReturn("User registered successfully");
        
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }
 
    @Test
    public void testRegisterUser_EmailAlreadyRegistered() {
   
        doThrow(new IllegalArgumentException("Email already registered"))
                .when(userService).registerUser(any(UserRegRequest.class));
 
   
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
 
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Email already registered", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus()); // Updated to match `status` field
    }
 
    @Test
    public void testRegisterUser_InvalidEmailFormat() {
 
        userRegRequest.setEmail("invalid-email");
        when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email format"));
 
 
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
 
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Invalid email format", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus()); // Updated to match `status` field
    }
 
    @Test
    public void testRegisterUser_InvalidMobileNumber() {
     
        userRegRequest.setMobilenumber("12345");
        when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new IllegalArgumentException("Mobile number must be 10 digits"));
 
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
 
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Mobile number must be 10 digits", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus()); // Updated to match `status` field
    }
 
    @Test
    public void testRegisterUser_ServerError() {
   
        when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));
 
     
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
 
    
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Unexpected error occurred", errorResponse.getMessage());
        assertEquals(500, errorResponse.getStatus()); // Updated to match `status` field
    }
 
    @Test
    public void testRegisterUser_MissingFirstName() {
 
        userRegRequest.setFirstname("");
        when(userService.registerUser(any(UserRegRequest.class)))
                .thenThrow(new IllegalArgumentException("First name is required"));
 
   
        ResponseEntity<?> response = userRegistrationController.registerUser(userRegRequest);
 
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("First name is required", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus());
    }
}
 
 
 