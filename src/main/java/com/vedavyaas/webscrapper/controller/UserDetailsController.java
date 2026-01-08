package com.vedavyaas.webscrapper.controller;

import com.vedavyaas.webscrapper.dto.JWTResponse;
import com.vedavyaas.webscrapper.dto.UserDTO;
import com.vedavyaas.webscrapper.service.MailService;
import com.vedavyaas.webscrapper.service.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class UserDetailsController {

    private final UserDetailsService userDetailsService;
    private final MailService mailService;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDetailsController(UserDetailsService userDetailsService, MailService mailService, JwtEncoder jwtEncoder, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.mailService = mailService;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    ResponseEntity<JWTResponse> authenticate(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.email(), userDTO.password()));
        String token = createToken(authentication);
        return ResponseEntity.ok(new JWTResponse(token));
    }

    private String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder().issuer("self").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60 * 10)).subject(authentication.getName()).claim("scope", createScope(authentication)).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
    }

    @PutMapping("/change/password")
    public String changePassword(@RequestParam String OTP, @RequestParam String newPassword) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDetailsService.resetPassword(user, OTP, newPassword);
    }

    @PostMapping("/create/account")
    public String createAccount(@RequestBody UserDTO userDTO, @RequestParam String OTP) {
        return userDetailsService.createAccount(userDTO, OTP);
    }

    @PutMapping("/forget/password")
    public String forgetPassword(@RequestParam String email, @RequestParam String OTP, @RequestParam String newPassword) {
        return userDetailsService.resetPassword(email, OTP, newPassword);
    }

    @GetMapping("/get/OTP")
    public String getOTP(@RequestParam String email) {
        return mailService.sendOTP(email);
    }
}
