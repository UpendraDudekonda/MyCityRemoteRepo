package com.mycity.email.service;

public interface UserEmailService {

	void generateAndSendOTP(String string);

	boolean verifyOTP(String email, String otp);

}
