package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.dto.UserDTO;
import com.vedavyaas.webscrapper.repository.OTPEntity;
import com.vedavyaas.webscrapper.repository.OTPRepository;
import com.vedavyaas.webscrapper.repository.UserEntity;
import com.vedavyaas.webscrapper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    private final String email = "testing@gmail.com";
    private final String otp = "123456";
    private final String password = "test";
    private final String newPassword = "password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private OTPRepository otpRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Test
    public void resetPasswordTest1() {
        UserEntity mockUser = new UserEntity(email, password);
        OTPEntity mockOTP = new OTPEntity(email, otp);
        mockOTP.setCreatedTime(LocalDateTime.now());

        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.of(mockUser));
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(new OTPEntity(email, otp));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        assertEquals("Password reset successfully", userDetailsService.resetPassword(email, otp, newPassword));
    }

    @Test
    public void resetPasswordTest2() {
        UserEntity mockUser = new UserEntity(email, password);
        OTPEntity mockOTP = new OTPEntity(email, otp);
        mockOTP.setCreatedTime(LocalDateTime.now().minus(Duration.ofHours(1)));

        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.of(mockUser));
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(mockOTP);

        assertEquals("OTP expired", userDetailsService.resetPassword(email, otp, newPassword));
    }

    @Test
    public void resetPasswordTest3() {
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.empty());
        assertEquals("User not found",  userDetailsService.resetPassword(email, otp, newPassword));
    }

    @Test
    public void resetPasswordTest4() {
        UserEntity mockUser = new UserEntity(email, password);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.of(mockUser));

        assertEquals("Invalid OTP",  userDetailsService.resetPassword(email, null, newPassword));
    }

    @Test
    public void createUserTest1() {
        UserDTO mockDTO = new UserDTO(email, password);
        assertEquals("OTP required", userDetailsService.createAccount(mockDTO, null));
    }

    @Test
    public void createUserTest2() {
        UserDTO mockDTO = new UserDTO(email, password);
        when(userRepository.existsUserEntityByEmail(email)).thenReturn(true);
        assertEquals("Email already in use",  userDetailsService.createAccount(mockDTO, otp));
    }

    @Test
    public void createUserTest3() {
        UserDTO mockDTO = new UserDTO(email, password);
        when(userRepository.existsUserEntityByEmail(email)).thenReturn(false);
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(null);
        assertEquals("Invalid OTP",  userDetailsService.createAccount(mockDTO, otp));
    }

    @Test
    public void createUserTest4() {
        UserDTO mockDTO = new UserDTO(email, password);
        OTPEntity mockOTP = new OTPEntity(email, otp);
        mockOTP.setCreatedTime(LocalDateTime.now().minus(Duration.ofHours(1)));

        when(userRepository.existsUserEntityByEmail(email)).thenReturn(false);
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(mockOTP);

        assertEquals("OTP expired",  userDetailsService.createAccount(mockDTO, otp));
    }

    @Test
    public void createUserTest5() {
        UserDTO mockDTO = new UserDTO(email, password);
        OTPEntity mockOTP = new OTPEntity(email, otp);
        mockOTP.setCreatedTime(LocalDateTime.now());

        when(userRepository.existsUserEntityByEmail(email)).thenReturn(false);
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(mockOTP);
        assertEquals("Invalid OTP",  userDetailsService.createAccount(mockDTO, "21"));
    }

    @Test
    public void createUserTest6() {
        UserDTO mockDTO = new UserDTO(email, password);
        OTPEntity mockOTP = new OTPEntity(email, otp);
        mockOTP.setCreatedTime(LocalDateTime.now());
        when(userRepository.existsUserEntityByEmail(email)).thenReturn(false);
        when(otpRepository.findOTPEntityByEmail(email)).thenReturn(mockOTP);
        when(passwordEncoder.encode(password)).thenReturn(password);

        assertEquals("User created",  userDetailsService.createAccount(mockDTO, otp));
    }
}
