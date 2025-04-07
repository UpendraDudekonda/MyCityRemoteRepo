package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.dto.UserLoginRequest;
import com.mycity.shared.dto.UserRegRequest;

@Service
public interface UserAuthenticationInterface {

	void registerUser(UserRegRequest request);


	String LoginUser(UserLoginRequest request);
	
	
}
