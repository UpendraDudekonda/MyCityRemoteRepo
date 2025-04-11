package com.mycity.email.service;

public interface MerchantEmailService {

	void generateAndSendOTP(String email);

	boolean verifyOTP(String email, String otp);

}
