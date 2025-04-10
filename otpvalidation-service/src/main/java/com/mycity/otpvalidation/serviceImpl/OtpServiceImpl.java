package com.mycity.otpvalidation.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mycity.otpvalidation.service.OtpService;

@Service
public class OtpServiceImpl  implements OtpService{

	    @Autowired
	    private RedisTemplate<String, String> redisTemplate;

	    // Method to verify OTP from Redis
	    public boolean verifyOtp(String email, String otp) {
	        String storedOtp = redisTemplate.opsForValue().get(email);  // Get OTP from Redis

	        if (storedOtp != null && storedOtp.equals(otp)) {
	            redisTemplate.delete(email);  // Remove OTP from Redis after successful verification
	            return true;
	        }
	        return false;
	    }

}
