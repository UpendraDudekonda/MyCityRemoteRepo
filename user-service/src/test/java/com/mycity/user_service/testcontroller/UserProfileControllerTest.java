package com.mycity.user_service.testcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.mycity.user.controller.UserProfileController;
import com.mycity.user.service.UserProfileInterface;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTest { // Renamed class for clarity

    @Mock
    private UserProfileInterface userProfile;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private MultipartFile mockImageFile;

    private String userId;
    private byte[] imageBytes;
    private String originalFilename;

    // Mock objects for WebClient call chain
    private WebClient mockWebClient;
    private RequestBodyUriSpec mockRequestBodyUriSpec; // For POST
    private RequestHeadersUriSpec mockRequestHeadersUriSpec; // For GET
    private RequestBodySpec mockRequestBodySpec; // For POST body

    // Declare these mocks with the specific generic types expected at each step
    // RequestBodySpec.bodyValue() returns RequestHeadersSpec<RequestBodySpec>
    private RequestHeadersSpec<RequestBodySpec> mockRequestHeadersSpecAfterBody;

    // RequestHeadersUriSpec.uri() returns RequestHeadersSpec<Void>
    private RequestHeadersSpec<?> mockRequestHeadersSpecAfterUriGet;

    private ResponseSpec mockResponseSpec; // For retrieve()

    @BeforeEach
    public void setUp() throws IOException {
        userId = "99";
        originalFilename = "profile.jpg";
        imageBytes = "fake image content".getBytes(StandardCharsets.UTF_8);

        when(mockImageFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockImageFile.getBytes()).thenReturn(imageBytes);

        // Initialize common WebClient mock objects
        mockWebClient = mock(WebClient.class);
        mockRequestBodyUriSpec = mock(RequestBodyUriSpec.class);
        mockRequestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
        mockRequestBodySpec = mock(RequestBodySpec.class);

        // Initialize mocks with their specific generic types
        mockRequestHeadersSpecAfterBody = mock(RequestHeadersSpec.class);
        mockRequestHeadersSpecAfterUriGet = mock(RequestHeadersSpec.class);

        mockResponseSpec = mock(ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(mockWebClient);
    }

    // --- Tests for uploadPictureToMediaService ---

//    @Test
//    public void testUploadPictureToMediaService_Success() {
//        // Arrange WebClient mocking for POST success
//        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
//        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
//        when(mockRequestBodySpec.contentType(eq(MediaType.MULTIPART_FORM_DATA))).thenReturn(mockRequestBodySpec);
//
//        // FIX: Cast the returned mock to the expected generic type RequestHeadersSpec<RequestBodySpec>
//        when(mockRequestBodySpec.bodyValue(any())).thenReturn((RequestHeadersSpec<RequestBodySpec>) mockRequestHeadersSpecAfterBody);
//
//        // Chain retrieve() from the correctly typed mock 
//        when(mockRequestHeadersSpecAfterBody.retrieve()).thenReturn(mockResponseSpec);
//
//        Mono<String> mockResponseMono = Mono.just("Upload successful: http://media-service/uploaded-image.jpg");
//        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(mockResponseMono);
//
//        // Act: Call the controller method
//        ResponseEntity<String> response = userProfileController.uploadPictureToMediaService(userId, mockImageFile);
//
//        // Assert: Verify the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Upload successful: http://media-service/uploaded-image.jpg", response.getBody());
//    }
//
//    @Test
//    public void testUploadPictureToMediaService_WebClientError() {
//        // Arrange WebClient mocking for an error scenario
//        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
//        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodySpec);
//        when(mockRequestBodySpec.contentType(eq(MediaType.MULTIPART_FORM_DATA))).thenReturn(mockRequestBodySpec);
//
//        // FIX: Cast the returned mock to the expected generic type RequestHeadersSpec<RequestBodySpec>
//        when(mockRequestBodySpec.bodyValue(any())).thenReturn((RequestHeadersSpec<RequestBodySpec>) mockRequestHeadersSpecAfterBody);
//
//        // Chain retrieve() from the correctly typed mock
//        when(mockRequestHeadersSpecAfterBody.retrieve()).thenReturn(mockResponseSpec);
//
//        // Simulate a Mono that throws an exception when blocked (simulates WebClient error)
//        Mono<String> errorMono = Mono.error(new RuntimeException("Simulated Media Service Error"));
//        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(errorMono);
//
//
//        // Act: Call the controller method
//        ResponseEntity<String> response = userProfileController.uploadPictureToMediaService(userId, mockImageFile);
//
//        // Assert: Verify the response for an error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("Upload failed: Simulated Media Service Error", response.getBody());
//    }

     @Test
    public void testUploadPictureToMediaService_IOException() throws IOException {
        // Arrange: Configure the mock MultipartFile to throw an IOException when getBytes() is called
        when(mockImageFile.getBytes()).thenThrow(new IOException("Simulated IO Exception during file read"));

        // Act: Call the controller method
        ResponseEntity<String> response = userProfileController.uploadPictureToMediaService(userId, mockImageFile);

        // Assert: Verify the response for an IO error
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // The body should contain the error message from the controller's catch block
        assertEquals("Upload failed: Simulated IO Exception during file read", response.getBody());
    }

    // --- Tests for getProfilePictureUrl ---

    @Test
    public void testGetProfilePictureUrl_Success() {
        // Arrange WebClient mocking for GET success
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        // FIX: Cast the returned mock to the expected generic type RequestHeadersSpec<Void>
        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn((RequestHeadersSpec<?>) mockRequestHeadersSpecAfterUriGet);
        // Chain retrieve() from the correctly typed mock
        when(mockRequestHeadersSpecAfterUriGet.retrieve()).thenReturn(mockResponseSpec);

        // Simulate a successful response entity from the media service
        ResponseEntity<String> successResponseEntity = ResponseEntity.ok("http://media-service/profile-pics/" + userId + ".jpg");
        Mono<ResponseEntity<String>> mockResponseMonoEntity = Mono.just(successResponseEntity);
        when(mockResponseSpec.toEntity(String.class)).thenReturn(mockResponseMonoEntity);

        // Act: Call the controller method, which returns a Mono
        Mono<ResponseEntity<String>> responseMono = userProfileController.getProfilePictureUrl(userId);

        // Block the Mono to get the actual ResponseEntity for assertion in a test
        ResponseEntity<String> response = responseMono.block();

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://media-service/profile-pics/" + userId + ".jpg", response.getBody());
    }

    @Test
    public void testGetProfilePictureUrl_MediaServiceError() {
        // Arrange WebClient mocking for GET error
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        // FIX: Cast the returned mock to the expected generic type RequestHeadersSpec<Void>
        when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn((RequestHeadersSpec<?>) mockRequestHeadersSpecAfterUriGet);
        // Chain retrieve() from the correctly typed mock
        when(mockRequestHeadersSpecAfterUriGet.retrieve()).thenReturn(mockResponseSpec);

        // Simulate an error response entity from the media service (e.g., 404 Not Found)
        ResponseEntity<String> errorResponseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        Mono<ResponseEntity<String>> mockResponseMonoEntity = Mono.just(errorResponseEntity);
        when(mockResponseSpec.toEntity(String.class)).thenReturn(mockResponseMonoEntity);

        // Act: Call the controller method
        Mono<ResponseEntity<String>> responseMono = userProfileController.getProfilePictureUrl(userId);

        // Block the Mono to get the actual ResponseEntity
        ResponseEntity<String> response = responseMono.block();

        // Assert: Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Image not found", response.getBody());
    }

    @Test
    public void testGetProfilePictureUrl_NumberFormatExceptionCaught() {
        // Arrange: Simulate an exception occurring within the try block.
        // We make the build() call throw the exception for test purposes.
        when(webClientBuilder.build()).thenThrow(new NumberFormatException("Simulated invalid number format"));

        // Act: Call the controller method
        Mono<ResponseEntity<String>> responseMono = userProfileController.getProfilePictureUrl(userId);

        // Block the Mono to get the actual ResponseEntity
        ResponseEntity<String> response = responseMono.block();

        // Assert: Verify the response from the catch block
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user ID format", response.getBody());
    }

    // --- Add tests for other methods in UserProfileController as needed ---
    // Example: testGetUserProfile_Success, testGetUserIdByName_Success, etc.
}