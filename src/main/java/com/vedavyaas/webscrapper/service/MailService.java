package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.repository.OTPEntity;
import com.vedavyaas.webscrapper.repository.OTPRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final OTPRepository otpRepository;

    public MailService(JavaMailSender mailSender, OTPRepository otpRepository) {
        this.mailSender = mailSender;
        this.otpRepository = otpRepository;
    }

    public String sendOTP(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("New OTP");
        SecureRandom sr = new SecureRandom();
        String otp = String.valueOf((100000 + sr.nextInt(900000)));
        mailMessage.setText("Your OTP is: " + otp);

        OTPEntity otpEntity = new OTPEntity(email, otp);

        if (otpRepository.existsByEmail(email)) otpRepository.deleteByEmail(email);
        otpRepository.save(otpEntity);

        mailSender.send(mailMessage);
        return "OTP sent successfully";
    }
}
