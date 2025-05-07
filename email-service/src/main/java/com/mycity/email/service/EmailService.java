package com.mycity.email.service;

public interface EmailService {

	

	void generateAndSendOTP(String email);

	boolean verifyOTP(String email, String otp);

}
