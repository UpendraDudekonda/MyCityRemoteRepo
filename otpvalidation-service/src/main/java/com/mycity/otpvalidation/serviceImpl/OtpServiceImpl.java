package com.mycity.otpvalidation.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mycity.otpvalidation.exception.ExpiredOtpException;
import com.mycity.otpvalidation.exception.InvalidOtpException;
import com.mycity.otpvalidation.service.OtpService;

@Service
public class OtpServiceImpl  implements OtpService{

	    @Autowired
	    private RedisTemplate<String, String> redisTemplate;

	    
	    public boolean verifyOtp(String email, String otp) {
	        String redisKey = "otp:" + email;
	        String storedOtp = redisTemplate.opsForValue().get(redisKey);
	        Long ttl = redisTemplate.getExpire(redisKey);
	        
	        System.err.println("OTP verification for email: " + email);
	        System.err.println("Redis key used: " + redisKey);
	        System.err.println("Stored OTP: " + storedOtp);
	        System.err.println("TTL of OTP: " + ttl);
	        
	        if (storedOtp == null) {
	            throw new ExpiredOtpException("OTP expired or not found.");
	        }

	        if (ttl == null || ttl <= 0) {
	            redisTemplate.delete(redisKey);
	            throw new ExpiredOtpException("OTP expired.");
	        }

	        if (!storedOtp.equals(otp)) {
	            throw new InvalidOtpException("OTP does not match.");
	        }

	        redisTemplate.delete(redisKey); // Remove OTP after successful verification
	        
	        return true;
	    }

}
