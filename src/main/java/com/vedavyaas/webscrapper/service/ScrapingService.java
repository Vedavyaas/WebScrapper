package com.vedavyaas.webscrapper.service;

import com.microsoft.playwright.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ScrapingService {

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));
            System.out.println("Playwright initialized successfully with Chromium.");
        } catch (Exception e) {
            System.err.println("Failed to initialize Playwright: " + e.getMessage());
            throw e;
        }
    }
    public BigDecimal calculatePrice(String url) {
        try(BrowserContext context = browser.newContext();
            Page page = context.newPage()) {

                page.navigate(url);
                Locator priceLocator = page.locator("text=/([\\$\\€\\£\\₹]\\s?\\d+([.,]\\d{2})?)/").first();
                priceLocator.waitFor(new Locator.WaitForOptions().setTimeout(10000));
                String rawPrice = priceLocator.innerText();
                String cleanedPrice = rawPrice.replaceAll("[^0-9,.]", "");
                if (cleanedPrice.contains(",") && !cleanedPrice.contains(".")) {
                    cleanedPrice = cleanedPrice.replace(",", ".");
                }

                return new BigDecimal(cleanedPrice);
        } catch(Exception e) {
            return new BigDecimal(0);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
