package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.user.dto.UserLoginRequest;
import com.mycity.user.dto.UserRegistrationRequest;

@Service
public interface UserServiceInterface {

	void registerUser(UserRegistrationRequest request);


	String LoginUser(UserLoginRequest request);

	
	
}
