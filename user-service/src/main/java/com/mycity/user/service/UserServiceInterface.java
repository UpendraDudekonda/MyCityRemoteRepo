package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.dto.*;

@Service
public interface UserServiceInterface {

	void registerUser(UserRegRequest request);


	String LoginUser(UserLoginRequest request);

	
	
}
