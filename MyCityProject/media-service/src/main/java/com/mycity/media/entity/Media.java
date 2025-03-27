package com.mycity.media.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "media")
public class Media {
	
	 private Long mediaId;
	    private String publicId; //Cloudinary public id, or S3 key
	    private String url;
	    private String fileType;
	    private Long fileSize;
	    private LocalDateTime uploadedAt;
	    private String uploadedBy; // User or service that uploaded the media
	    // Add other relevant metadata

	    // Getters and setters...
	


}
