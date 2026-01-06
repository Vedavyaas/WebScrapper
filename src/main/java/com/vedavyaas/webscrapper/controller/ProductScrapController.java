package com.vedavyaas.webscrapper.controller;

import com.vedavyaas.webscrapper.repository.ProductScrapEntity;
import com.vedavyaas.webscrapper.service.ProductScrapService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ProductScrapController {

    private final ProductScrapService productScrapService;

    public ProductScrapController(ProductScrapService productScrapService) {
        this.productScrapService = productScrapService;
    }

    @PostMapping("/post/url")
    public String putUrl(@RequestParam String url, @RequestParam BigDecimal targetPrice) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return productScrapService.putUrl(user, url, targetPrice);
    }

    @GetMapping("/get/url")
    public List<ProductScrapEntity> getUrl() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return productScrapService.getUrl(user);
    }

    @PutMapping("/change/url")
    public String changeUrl(@RequestParam String url, @RequestParam Long id) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return productScrapService.changeUrl(user, url, id);
    }

    @PutMapping("/change/price")
    public String changePrice(@RequestParam BigDecimal newPrice, @RequestParam Long id) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return productScrapService.changeTargetPrice(user, newPrice, id);
    }

    @DeleteMapping("/delete/scrap")
    public String deleteScrap(@RequestParam Long id) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return productScrapService.deleteEntity(user, id);
    }
}
