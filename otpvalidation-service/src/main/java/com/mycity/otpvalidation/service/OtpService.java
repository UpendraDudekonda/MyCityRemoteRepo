package com.mycity.otpvalidation.service;

public interface OtpService {

	boolean verifyOtp(String email, String otp);

}
