package com.mycity.media.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.media.entity.UserProfileImg;



public interface UserProfileImgRepository extends JpaRepository<UserProfileImg, Long> {
	

	
	Optional<UserProfileImg> findByUserId(String userId);
}
