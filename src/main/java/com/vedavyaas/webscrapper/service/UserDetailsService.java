package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.dto.UserDTO;
import com.vedavyaas.webscrapper.repository.OTPEntity;
import com.vedavyaas.webscrapper.repository.OTPRepository;
import com.vedavyaas.webscrapper.repository.UserEntity;
import com.vedavyaas.webscrapper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserDetailsService {
    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsService(UserRepository userRepository, OTPRepository otpRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String resetPassword(String email, String otp, String newPassword) {
        Optional<UserEntity> user = userRepository.findUserEntityByEmail(email);
        if (user.isPresent()) {
            OTPEntity OTPEntity = otpRepository.findOTPEntityByEmail(email);
            if (OTPEntity != null) {
                LocalDateTime createdTime = OTPEntity.getCreatedTime();
                if (createdTime == null || createdTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
                    otpRepository.delete(OTPEntity);
                    return "OTP expired";
                }
            }
            if(OTPEntity != null && OTPEntity.getOTP().equals(otp)) {
                user.get().setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user.get());
                otpRepository.delete(OTPEntity);
                return "Password reset successfully";
            }
            return "Invalid OTP";
        }
        return "User not found";
    }

    public String createAccount(UserDTO userDTO, String otp) {
        if (otp == null || otp.isBlank()) {
            return "OTP required";
        }

        if (userRepository.existsUserEntityByEmail(userDTO.email())) {
            return "Email already in use";
        }

        OTPEntity otpEntity = otpRepository.findOTPEntityByEmail(userDTO.email());
        if (otpEntity == null) {
            return "Invalid OTP";
        }

        LocalDateTime createdTime = otpEntity.getCreatedTime();
        if (createdTime == null || createdTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            otpRepository.delete(otpEntity);
            return "OTP expired";
        }

        if (!otpEntity.getOTP().equals(otp)) {
            return "Invalid OTP";
        }

        userRepository.save(new UserEntity(userDTO.email(), passwordEncoder.encode(userDTO.password())));
        otpRepository.delete(otpEntity);
        return "User created";
    }
}
