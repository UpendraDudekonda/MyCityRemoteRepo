package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.user.UserResponseDTO;

@Service
public interface UserProfileInterface {


	UserResponseDTO getUserById(String userId);

	
	
}
