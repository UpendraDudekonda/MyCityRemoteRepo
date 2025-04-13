package com.mycity.email.serviceImpl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mycity.email.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String OTP_PREFIX = "otp:";
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 10;

    // Generate OTP and send it via email
    public void generateAndSendOTP(String recipientEmail) {
        String otp = generateRandomOTP(OTP_LENGTH);

        // Store OTP in Redis with an expiry time
        String redisKey = OTP_PREFIX + recipientEmail;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // Send OTP via email
        sendOTPEmail(recipientEmail, otp);
    }

//    // Helper method to generate a random 6-digit OTP
//    private String generateOtp() {
//        int otp = (int) (Math.random() * 1000000); // Generate a 6-digit random number
//        return String.format("%06d", otp);         // Pad with leading zeros if needed
//    }
    
      
    // Send OTP via email
    private void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for MyCity Registration");
        message.setText("Your OTP is: " + otp + ". This OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
        mailSender.send(message);
    }


    // Generate a random OTP of a specified length
    private String generateRandomOTP(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Add a random digit to the OTP
        }
        return otp.toString();
    }

	

  

}
