package com.mycity.user.controller;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.shared.userdto.UserDTO;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.service.UserProfileInterface;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@AllArgsConstructor

public class UserProfileController {

    private final UserProfileInterface userProfile;
    
    private final WebClient.Builder webClientBuilder;

    @PostMapping("/profile/upload-picture")
    public ResponseEntity<String> uploadPictureToMediaService(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            ByteArrayResource imageResource = new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("form-data")
                    .name("image")
                    .filename(imageFile.getOriginalFilename())
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<ByteArrayResource> imagePart = new HttpEntity<>(imageResource, headers);

            body.add("image", imagePart);
            body.add("userId", userId);

            String response = webClientBuilder.build()
                    .post()
                    .uri("lb://media-service/media/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/profile-picture")
    public Mono<ResponseEntity<String>> getProfilePictureUrl(@RequestHeader("X-User-Id") String userId) {
        try {
           
            return webClientBuilder.build()
                    .get()
                    .uri("lb://media-service/media/get-image/" + userId)
                    .retrieve()
                    .toEntity(String.class);
        } catch (NumberFormatException e) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user ID format"));
        }
    }

   

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("X-User-Id") String userId) {
        UserResponseDTO user = userProfile.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/getuserId/{userName}") //to get userId using userName while adding review to the place 
    public ResponseEntity<Long> getUSerIdByName(@PathVariable String userName)
    {
    	System.out.println("UserProfileController.getUSerIdByName()");
    	//use service
    	Long userID=userProfile.getUserIdByUserName(userName);
    	return new ResponseEntity<Long>(userID,HttpStatus.OK);
    }
    
   //ADMIN 
   @PutMapping("/updateuser/{userId}") 
   public ResponseEntity<String> updateUser(@RequestBody UserDTO dto,@PathVariable String userId)
   {
	  //use service
	  return new ResponseEntity<String>(userProfile.updateUser(userId,dto),HttpStatus.OK);
   }
   
   //ADMIN
   @DeleteMapping("/deleteuser/{userId}")
   public ResponseEntity<String> deleteUser(@PathVariable String userId)
   {
	   //use service
	   return new ResponseEntity<String>(userProfile.deleteUser(userId),HttpStatus.OK);
   }
   
   @PatchMapping("/updatepassword")
   public ResponseEntity<String> updatePassword(@RequestBody UserLoginRequest req)
   {
	   //use service
	   return new ResponseEntity<String>(userProfile.changePassword(req),HttpStatus.ACCEPTED);
   }
    
}
