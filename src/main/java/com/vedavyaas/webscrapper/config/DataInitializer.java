package com.vedavyaas.webscrapper.config;

import com.vedavyaas.webscrapper.assets.Role;
import com.vedavyaas.webscrapper.repository.UserEntity;
import com.vedavyaas.webscrapper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.enabled:true}") boolean enabled,
            @Value("${app.seed.user.email:user@webscrapper.local}") String userEmail,
            @Value("${app.seed.user.password:user123}") String userPassword,
            @Value("${app.seed.admin.email:admin@webscrapper.local}") String adminEmail,
            @Value("${app.seed.admin.password:admin123}") String adminPassword
    ) {
        return args -> {
            if (!enabled) return;

            if (!userRepository.existsUserEntityByEmail(userEmail)) {
                UserEntity user = new UserEntity(userEmail, passwordEncoder.encode(userPassword));
                user.setRole(Role.USER);
                userRepository.save(user);
            }

            if (!userRepository.existsUserEntityByEmail(adminEmail)) {
                UserEntity admin = new UserEntity(adminEmail, passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
