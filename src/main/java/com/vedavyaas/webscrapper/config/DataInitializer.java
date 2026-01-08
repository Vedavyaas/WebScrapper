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
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/a-light-in-the-attic_1000/index.html", new BigDecimal("51.77")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/tipping-the-velvet_999/index.html", new BigDecimal("53.74")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/soumission_998/index.html", new BigDecimal("50.10")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/sharp-objects_997/index.html", new BigDecimal("47.82")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/sapiens-a-brief-history-of-humankind_996/index.html", new BigDecimal("54.23")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-requiem-red_995/index.html", new BigDecimal("22.65")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-dirty-little-secrets-of-getting-your-dream-job_994/index.html", new BigDecimal("33.34")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-coming-woman-a-novel-based-on-the-life-of-the-infamous-feminist-victoria-woodhull_993/index.html", new BigDecimal("17.93")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-boys-in-the-boat-nine-americans-and-their-epic-quest-for-gold-at-the-1936-berlin-olympics_992/index.html", new BigDecimal("22.60")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-black-maria_991/index.html", new BigDecimal("52.15"))
            );
            List<ProductScrapEntity> adminSamples = List.of(
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/593", new BigDecimal("1143.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/583", new BigDecimal("103.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/584", new BigDecimal("113.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/590", new BigDecimal("1139.54")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/592", new BigDecimal("1133.41")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/574", new BigDecimal("433.23")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/575", new BigDecimal("441.56")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/580", new BigDecimal("484.23")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/570", new BigDecimal("295.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/571", new BigDecimal("299.00"))
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
