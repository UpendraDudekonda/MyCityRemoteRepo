package com.mycity.media.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="adminprofileImage")
@Data
public class AdminProfileImg {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String adminId;
    private String imageUrl;

}
