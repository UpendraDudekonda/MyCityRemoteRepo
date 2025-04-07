package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserRegRequest;

@Service
public interface UserServiceInterface {

	void registerUser(UserRegRequest request);


	String LoginUser(UserLoginRequest request);

	
	
}
