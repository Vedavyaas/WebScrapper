package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.repository.OTPRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private OTPRepository otpRepository;
    @InjectMocks
    private MailService mailService;

    @Test
    public void testSendOTP(){
        String email = "test@gmail.com";
        when(otpRepository.existsByEmail(email)).thenReturn(true);

        assertEquals("OTP sent successfully", mailService.sendOTP(email));
    }
}
