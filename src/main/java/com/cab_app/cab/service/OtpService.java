package com.cab_app.cab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class OtpService {

    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5); // 5-minute validity

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Generate and store OTP
    public String generateOtp(String phoneNo) {
        String otp = String.valueOf(1000 + new Random().nextInt(9000)); // 4-digit OTP
        String redisKey = getKey(phoneNo);
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRY);
        System.out.println("Generated OTP for " + phoneNo + ": " + otp); // Log it for now
        return otp;
    }

    // Validate OTP
    public boolean verifyOtp(String phoneNo, String inputOtp) {
        String redisKey = getKey(phoneNo);
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            redisTemplate.delete(redisKey); // OTP can be used only once
            return true;
        }
        return false;
    }

    // Helper to generate key
    private String getKey(String phoneNo) {
        return "OTP:" + phoneNo;
    }
}
