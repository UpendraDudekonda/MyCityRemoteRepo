package com.mycity.media.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mycity.media.service.UserProfileImgService;



@RestController
@RequestMapping("/media")
@Slf4j
@RequiredArgsConstructor
public class UserProfileImageController {
	
    private  final UserProfileImgService profileImgService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadUserImage(@RequestParam("userId") String userId,
                                                  @RequestParam("image") MultipartFile file
                                                 ) {
    	profileImgService.uploadUserImage(file, userId);
        return ResponseEntity.ok("Image uploaded successfully...!!");

    }
    
    @GetMapping("/get-image/{userId}")
    public ResponseEntity<String> getImageByUserId(@PathVariable String userId) {
        String url = profileImgService.getImageUrlByUserId(userId);
        return ResponseEntity.ok(url);
    }
    
}
