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
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/sapiens-996/index.html", new BigDecimal("54.23")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-requiem-red_995/index.html", new BigDecimal("22.65")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/dream-job_994/index.html", new BigDecimal("33.34")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-coming-woman_993/index.html", new BigDecimal("17.93")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/boys-in-the-boat_992/index.html", new BigDecimal("22.60")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-black-maria_991/index.html", new BigDecimal("52.15")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/star-wars_990/index.html", new BigDecimal("29.99")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/world-war-z_989/index.html", new BigDecimal("35.50")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-great-gatsby_988/index.html", new BigDecimal("15.99")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/1984-orwell_987/index.html", new BigDecimal("12.40")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/animal-farm_986/index.html", new BigDecimal("10.50")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/brave-new-world_985/index.html", new BigDecimal("18.20")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-hobbit_984/index.html", new BigDecimal("25.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/dune-herbert_983/index.html", new BigDecimal("42.15")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/foundation-asimov_982/index.html", new BigDecimal("38.90")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/neuromancer_981/index.html", new BigDecimal("27.45")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/snow-crash_980/index.html", new BigDecimal("21.30")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-shining_979/index.html", new BigDecimal("19.95")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/it-stephen-king_978/index.html", new BigDecimal("55.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/dracula_977/index.html", new BigDecimal("14.75")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/frankenstein_976/index.html", new BigDecimal("11.20")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-alchemist_975/index.html", new BigDecimal("23.40")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-road_974/index.html", new BigDecimal("31.10")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/american-gods_973/index.html", new BigDecimal("28.80")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/good-omens_972/index.html", new BigDecimal("20.15")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-martian_971/index.html", new BigDecimal("16.99")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/project-hail-mary_970/index.html", new BigDecimal("45.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/ready-player-one_969/index.html", new BigDecimal("19.50")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-silk-roads_968/index.html", new BigDecimal("34.60")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/homo-deus_967/index.html", new BigDecimal("41.25")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/guns-germs-steel_966/index.html", new BigDecimal("26.90")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-selfish-gene_965/index.html", new BigDecimal("22.40")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/cosmos-sagan_964/index.html", new BigDecimal("30.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/brief-history-time_963/index.html", new BigDecimal("19.99")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-gene-mukherjee_962/index.html", new BigDecimal("48.50")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/thinking-fast-slow_961/index.html", new BigDecimal("37.10")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/atomic-habits_960/index.html", new BigDecimal("24.95")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/deep-work_959/index.html", new BigDecimal("21.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/quiet-susan-cain_958/index.html", new BigDecimal("18.80")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-power-of-habit_957/index.html", new BigDecimal("20.50")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/blink-gladwell_956/index.html", new BigDecimal("17.40")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/outliers-gladwell_955/index.html", new BigDecimal("16.90")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-tipping-point_954/index.html", new BigDecimal("15.20")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/freakonomics_953/index.html", new BigDecimal("22.30")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/the-lean-startup_952/index.html", new BigDecimal("32.00")),
                    new ProductScrapEntity(userEmail, "https://books.toscrape.com/catalogue/zero-to-one_951/index.html", new BigDecimal("28.50"))
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
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/571", new BigDecimal("299.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/560", new BigDecimal("899.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/561", new BigDecimal("950.50")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/562", new BigDecimal("1020.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/563", new BigDecimal("1100.25")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/564", new BigDecimal("750.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/565", new BigDecimal("680.40")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/566", new BigDecimal("420.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/567", new BigDecimal("350.15")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/568", new BigDecimal("210.80")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/569", new BigDecimal("199.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/550", new BigDecimal("125.50")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/551", new BigDecimal("145.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/552", new BigDecimal("175.25")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/553", new BigDecimal("185.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/554", new BigDecimal("220.60")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/555", new BigDecimal("310.45")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/556", new BigDecimal("340.90")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/557", new BigDecimal("399.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/558", new BigDecimal("450.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/559", new BigDecimal("520.30")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/540", new BigDecimal("610.70")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/541", new BigDecimal("640.20")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/542", new BigDecimal("690.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/543", new BigDecimal("725.50")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/544", new BigDecimal("780.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/545", new BigDecimal("815.40")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/546", new BigDecimal("860.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/547", new BigDecimal("910.20")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/548", new BigDecimal("945.80")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/549", new BigDecimal("999.99")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/530", new BigDecimal("1050.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/531", new BigDecimal("1080.45")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/532", new BigDecimal("1115.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/533", new BigDecimal("1140.20")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/534", new BigDecimal("1170.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/535", new BigDecimal("1195.50")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/536", new BigDecimal("1200.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/537", new BigDecimal("105.00")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/538", new BigDecimal("115.30")),
                    new ProductScrapEntity(adminEmail, "https://webscraper.io/test-sites/e-commerce/allinone/product/539", new BigDecimal("125.80"))
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
