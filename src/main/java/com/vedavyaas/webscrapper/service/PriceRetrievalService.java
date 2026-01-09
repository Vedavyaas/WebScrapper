package com.vedavyaas.webscrapper.service;

import com.vedavyaas.webscrapper.repository.ProductScrapEntity;
import com.vedavyaas.webscrapper.repository.ProductScrapRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceRetrievalService {
    private final ProductScrapRepository productScrapRepository;
    private final ScrapingService scrapingService;

    @Autowired
    @Lazy
    private PriceRetrievalService self;
    private final Logger logger = LoggerFactory.getLogger(PriceRetrievalService.class);
    //private final JavaMailSenderImpl mailSender;

    public PriceRetrievalService(ProductScrapRepository productScrapRepository, ScrapingService scrapingService, JavaMailSenderImpl mailSender) {
        this.productScrapRepository = productScrapRepository;
        this.scrapingService = scrapingService;
        //this.mailSender = mailSender;
    }

    @Scheduled(initialDelay = 1_000, fixedRate = 60_000)
    public void fetchPrice() {
        Page<ProductScrapEntity> productScrapEntityPage;
        int page = 0;
        do {
            productScrapEntityPage = productScrapRepository.findAll(PageRequest.of(page, 10));

            for (var i : productScrapEntityPage.getContent()) {
                self.fetchPriceAsync(i);
            }
            page++;
        } while (productScrapEntityPage.hasNext());
    }

    @Async("AsyncScrapper")
    @Transactional
    public void fetchPriceAsync(ProductScrapEntity i) {
        try {
            BigDecimal currentPrice = scrapingService.calculatePrice(i.getUrl());
            i.setCurrentPrice(currentPrice);
            productScrapRepository.save(i);
            informUser(i);
        } catch (Exception e) {
            logger.warn("Failed to fetch price for url={}", i.getUrl(), e);
        }
    }

    public void informUser(ProductScrapEntity i) {
        if (i.getCurrentPrice() == null) return;
        if (i.getCurrentPrice().compareTo(i.getTargetPrice()) <= 0) {
            logger.info("{} , the price of this is lower than target price", i.getUrl());
//            try{
//                  SimpleMailMessage mailMessage = new SimpleMailMessage();
//                  mailMessage.setTo(i.getEmail());
//                  mailMessage.setSubject("Price Reduced");
//                  mailMessage.setText("Price is lower than your target price for this ." + i.getUrl());
//                  mailSender.send(mailMessage);
//            }catch(Exception e){
//                //ignore
//            }
            productScrapRepository.delete(i);
        }
    }
}