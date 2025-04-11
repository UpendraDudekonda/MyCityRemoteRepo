package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserRegRequest;

@Service
public interface UserServiceInterface {

	void startRegistration(UserRegRequest request);

	void completeRegistration(UserRegRequest request, String otp);

	String LoginUser(UserLoginRequest request);

	
	
}
