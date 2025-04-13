package com.mycity.user.service;

import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.user.entity.User;

public interface UserAuthenticationInterface {

	String registerUser(UserRegRequest request);


	User loginUser(String email, String password);


	void startRegistration(RequestOtpDTO request);


	void completeRegistration(UserRegRequest request, String otp);
	
	
}
