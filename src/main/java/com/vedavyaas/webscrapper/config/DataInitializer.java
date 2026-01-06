package com.vedavyaas.webscrapper.config;

import com.vedavyaas.webscrapper.assets.Role;
import com.vedavyaas.webscrapper.repository.ProductScrapEntity;
import com.vedavyaas.webscrapper.repository.ProductScrapRepository;
import com.vedavyaas.webscrapper.repository.UserEntity;
import com.vedavyaas.webscrapper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            ProductScrapRepository productScrapRepository,
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

            List<ProductScrapEntity> userSamples = List.of(
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/iphone-15-128gb", new BigDecimal("69999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/sony-wh1000xm5", new BigDecimal("21999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/samsung-990-pro-2tb", new BigDecimal("13999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/nike-air-max-90", new BigDecimal("5999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/boat-rockerz-450", new BigDecimal("1299.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/dell-inspiron-14-i5", new BigDecimal("54999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/lenovo-tab-m10", new BigDecimal("9999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/logitech-mx-master-3s", new BigDecimal("7999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/philips-airfryer-xl", new BigDecimal("8999.00")),
                    new ProductScrapEntity(userEmail, "https://store.example.com/p/oneplus-buds-3", new BigDecimal("3999.00"))
            );

            List<ProductScrapEntity> adminSamples = List.of(
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/asus-rt-ax55-router", new BigDecimal("4999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/apple-watch-se-44mm", new BigDecimal("24999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/kindle-paperwhite", new BigDecimal("10999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/bose-soundlink-flex", new BigDecimal("9999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/msi-gaming-monitor-27", new BigDecimal("15999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/instant-pot-6l", new BigDecimal("6999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/canon-eos-r50", new BigDecimal("59999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/fitbit-charge-6", new BigDecimal("11999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/anker-737-powerbank", new BigDecimal("8999.00")),
                    new ProductScrapEntity(adminEmail, "https://store.example.com/p/ps5-dualsense-controller", new BigDecimal("4999.00"))
            );

            for (ProductScrapEntity sample : userSamples) {
                if (!productScrapRepository.existsByEmailAndUrl(sample.getEmail(), sample.getUrl())) {
                    productScrapRepository.save(sample);
                }
            }

            for (ProductScrapEntity sample : adminSamples) {
                if (!productScrapRepository.existsByEmailAndUrl(sample.getEmail(), sample.getUrl())) {
                    productScrapRepository.save(sample);
                }
            }
        };
    }
}
