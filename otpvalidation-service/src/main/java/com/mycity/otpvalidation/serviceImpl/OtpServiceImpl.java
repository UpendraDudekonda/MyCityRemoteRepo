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
	        String storedOtp = redisTemplate.opsForValue().get("otp:" + email); // Ensure the key format matches
	        Long ttl = redisTemplate.getExpire("otp:" + email); // Check TTL

	        if (storedOtp == null) {
	            throw new ExpiredOtpException("OTP expired or not found.");
	        }

	        if (ttl <= 0) { // If TTL is 0 or less, it means OTP has expired
	            throw new ExpiredOtpException("OTP expired.");
	        }

	        if (!storedOtp.equals(otp)) {
	            throw new InvalidOtpException("OTP does not match.");
	        }

	        redisTemplate.delete("otp:" + email); // Remove OTP after successful verification
	        return true;
	    }

}
