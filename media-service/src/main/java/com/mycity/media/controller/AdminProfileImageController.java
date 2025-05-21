package com.mycity.media.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; 

import com.mycity.media.service.AdminProfileImgService;

import lombok.AllArgsConstructor;



@RestController
@RequestMapping("/media")

@AllArgsConstructor
public class AdminProfileImageController {
	
	private static final Logger log = LoggerFactory.getLogger(AdminProfileImageController.class);
	
	@Autowired
	 private  final AdminProfileImgService profileImgService;

	    @PostMapping("/admin/upload")
	    public ResponseEntity<String> uploadUserImage(@RequestParam("userId") String adminId,
	                                                  @RequestParam("image") MultipartFile file
	                                                 ) {
	    	log.info("The admin with id {} added the profile picture.",adminId);
	    	profileImgService.uploadUserImage(file, adminId);
	        return ResponseEntity.ok("Image uploaded successfully...!!");

	    }
	    
	    @GetMapping("/admin/get-image/{adminId}")
	    public ResponseEntity<String> getImageByUserId(@PathVariable String adminId) {
	        String url = profileImgService.getImageUrlByAdminId(adminId);
	        log.info("The admin with id {} fetched the profile picture.",adminId);
	        return ResponseEntity.ok(url);
	    }

}
