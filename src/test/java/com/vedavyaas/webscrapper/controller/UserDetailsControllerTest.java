package com.vedavyaas.webscrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedavyaas.webscrapper.dto.UserDTO;
import com.vedavyaas.webscrapper.service.MailService;
import com.vedavyaas.webscrapper.service.UserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(UserDetailsController.class)
public class UserDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtEncoder jwtEncoder;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private MailService mailService;

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testChangePassword() throws Exception {
        String otp = "123456";
        String newPassword = "newPassword";

        when(userDetailsService.resetPassword("test@gmail.com", otp, newPassword)).thenReturn("Password reset successfully");

        mockMvc.perform(put("/change/password").with(csrf()).param("OTP", "123456").param("newPassword", newPassword)).andExpect(status().isOk()).andExpect(content().string("Password reset successfully"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    public void testCreateAccount() throws Exception {
        String otp = "123456";
        String password = "Password";

        when(userDetailsService.createAccount(new UserDTO("test@gmail.com", password), otp)).thenReturn("Account created successfully");

        mockMvc.perform(post("/create/account").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new UserDTO("test@gmail.com", password))).param("OTP", otp)).andExpect(status().isOk()).andExpect(content().string("Account created successfully"));
    }

    @Test
    @WithMockUser
    public void testForgetPassword() throws Exception {
        String otp = "123456";
        String newPassword = "newPassword";

        when(userDetailsService.resetPassword("test@gmail.com", otp, newPassword)).thenReturn("Password reset successfully");

        mockMvc.perform(put("/forget/password").with(csrf()).param("email", "test@gmail.com").param("OTP", otp).param("newPassword", newPassword)).andExpect(status().isOk()).andExpect(content().string("Password reset successfully"));
    }

    @Test
    @WithMockUser
    public void testGetOTP() throws Exception {
        when(mailService.sendOTP("test@gmail.com")).thenReturn("OTP sent successfully");

        mockMvc.perform(get("/get/OTP").with(csrf()).param("email", "test@gmail.com")).andExpect(status().isOk()).andExpect(content().string("OTP sent successfully"));
    }
}