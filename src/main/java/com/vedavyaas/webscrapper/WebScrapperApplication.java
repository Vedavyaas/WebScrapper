package com.vedavyaas.webscrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebScrapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebScrapperApplication.class, args);
    }

}
