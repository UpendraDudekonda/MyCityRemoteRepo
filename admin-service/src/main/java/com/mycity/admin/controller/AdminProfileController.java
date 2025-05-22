package com.mycity.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycity.admin.service.AdminProfileService;
import com.mycity.shared.admindto.AdminProfileResponse;
import com.mycity.shared.admindto.AdminProfileUpdateDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminProfileController {
	
	private static final Logger log  =  LoggerFactory.getLogger(AdminProfileController.class);

    private final AdminProfileService adminProfile;
    
    private final WebClient.Builder webClientBuilder;

//    public AdminProfileController(AdminProfileService adminProfile) {
//        this.adminProfile = adminProfile;
//    }
    
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
            
            log.info("admin with id {} routing the profile uploaing to media-service",userId);

            String response = webClientBuilder.build()
                    .post()
                    .uri("lb://media-service/media/admin/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response); 

        } catch (Exception e) {
        	
        	 log.error("admin with id {} failed to routing the profile uploaing to media-service",userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/profile-picture")
    public Mono<ResponseEntity<String>> getProfilePictureUrl(@RequestHeader("X-User-Id") String userId) {
        try {
        	
        	log.info("admin with id {} is routing to media to fetch profil picture",userId);
           
            return webClientBuilder.build()
                    .get()
                    .uri("lb://media-service/media/admin/get-image/" + userId)
                    .retrieve()
                    .toEntity(String.class);
        } catch (NumberFormatException e) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid user ID format"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<AdminProfileResponse> getAdminProfile(@RequestHeader("X-User-Id") String adminId) {

        System.out.println("admin profile request got into methods for retrieving");

        AdminProfileResponse admin = adminProfile.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }
    
    @PatchMapping("/profile/update")
    public ResponseEntity<String> updateAdminDetails(
        @RequestHeader("X-User-Id") String adminId,
        @RequestBody AdminProfileUpdateDTO request
    ) {
    	
    	System.err.println("admin id : " + adminId);
    	adminProfile.updateAdminFields(adminId, request);
        return ResponseEntity.ok("Admin details updated successfully!");
    }

}