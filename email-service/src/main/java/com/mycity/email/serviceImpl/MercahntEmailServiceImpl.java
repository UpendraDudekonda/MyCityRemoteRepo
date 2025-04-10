package com.mycity.email.serviceImpl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mycity.email.service.MerchantEmailService;

@Service
public class MercahntEmailServiceImpl implements MerchantEmailService {
	@Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    private static final String OTP_PREFIX = "otp:";
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5; // OTP expiry time in minutes

    // Generate OTP and send it via email
    public void generateAndSendOTP(String recipientEmail) {
        String otp = generateRandomOTP(OTP_LENGTH);

        // Store OTP in Redis with an expiry time
        String redisKey = OTP_PREFIX + recipientEmail;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // Send OTP via email
        sendOTPEmail(recipientEmail, otp);
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

    // Send OTP via email
    private void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for MyCity Registration");
        message.setText("Your OTP is: " + otp + ". This OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
        mailSender.send(message);
    }

    // Verify OTP stored in Redis
    public boolean verifyOTP(String recipientEmail, String otp) {
        String redisKey = OTP_PREFIX + recipientEmail;
        String storedOTP = redisTemplate.opsForValue().get(redisKey);

        if (storedOTP != null && storedOTP.equals(otp)) {
            redisTemplate.delete(redisKey); // OTP is valid, so delete it
            return true;
        }
        return false;
    }

}
