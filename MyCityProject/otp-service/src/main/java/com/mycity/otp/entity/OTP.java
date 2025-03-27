package com.mycity.otp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

@Entity
public class OTP {
	
	
	 public Long otpId;
	    public String userId;
	    public String otp;
	    public LocalDateTime expiryTime;
	    public boolean verified;
	    


}
