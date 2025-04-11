package com.mycity.email.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;

//    private static final String OTP_PREFIX = "otp:";
//    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;

    // Method to generate OTP and send it via email
    public void generateAndSendOtp(String email) {
        String otp = generateOtp();  // Generate OTP
        sendOtpEmail(email, otp);    // Send OTP to email

        // Store OTP in Redis with expiration time
        redisTemplate.opsForValue().set(email, otp, OTP_EXPIRY_MINUTES , TimeUnit.MINUTES);
    }

    // Helper method to generate a random 6-digit OTP
    private String generateOtp() {
        int otp = (int) (Math.random() * 1000000); // Generate a 6-digit random number
        return String.format("%06d", otp);         // Pad with leading zeros if needed
    }

    // Helper method to send OTP to the provided email
    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Registration");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);  // Send the email
    }

}
